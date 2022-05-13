package edu.lysak.kafkastreams.streamingAnalytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lysak.kafkastreams.streamingAnalytics.db.MariaDBManager;
import edu.lysak.kafkastreams.streamingAnalytics.domain.SalesOrder;
import edu.lysak.kafkastreams.streamingAnalytics.service.KafkaOrdersDataGenerator;
import edu.lysak.kafkastreams.streamingAnalytics.service.OrderAggregator;
import edu.lysak.kafkastreams.util.ClassDeSerializer;
import edu.lysak.kafkastreams.util.ClassSerializer;
import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/****************************************************************************
 * This is an example for Streaming Analytics in Kafka Streams.
 * It reads a real time orders stream from kafka, performs periodic summaries
 * and writes the output the a JDBC sink.
 ****************************************************************************/

public class StreamingAnalytics {

    public static void main(String[] args) {
        //Initiate MariaDB DB Tracker and start the thread to print
        //summaries every 5 seconds
        MariaDBManager dbTracker = new MariaDBManager();
        dbTracker.setUp();
        Thread dbThread = new Thread(dbTracker);
        dbThread.start();

        //Create another MariaDB Connection to update data
        MariaDBManager dbUpdater = new MariaDBManager();
        dbUpdater.setUp();

        //Initiate the Kafka Orders Generator
        KafkaOrdersDataGenerator ordersGenerator = new KafkaOrdersDataGenerator();
        Thread generatorThread = new Thread(ordersGenerator);
        generatorThread.start();

        System.out.println("******** Starting Streaming  *************");

        try {
            /**************************************************
             * Build a Kafka Streams Topology
             **************************************************/

            //Setup Serializer / DeSerializer for Data types
            Serde<String> stringSerde = Serdes.String();
            Serde<Long> longSerde = Serdes.Long();

            Serde<SalesOrder> orderSerde = Serdes.serdeFrom(
                    new ClassSerializer<>(),
                    new ClassDeSerializer<>(SalesOrder.class)
            );
            Serde<OrderAggregator> aggregatorSerde = Serdes.serdeFrom(
                    new ClassSerializer<>(),
                    new ClassDeSerializer<>(OrderAggregator.class)
            );

            //Setup Properties for the Kafka Input Stream
            Properties properties = new Properties();
            properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "streaming-analytics-pipe");
            properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.SERVER);
            properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //For immediate results during testing
            properties.setProperty(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
            properties.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100");

            //Initiate the Kafka Streams Builder
            StreamsBuilder builder = new StreamsBuilder();

            //Create the source node for Orders
            KStream<String, String> ordersInput = builder.stream(
                    Constants.ORDERS_INPUT_TOPIC,
                    Consumed.with(stringSerde, stringSerde)
            );

            ObjectMapper mapper = new ObjectMapper();

            //Convert input json to SalesOrder object using Object Mapper
            KStream<String, SalesOrder> orderObjects = ordersInput.mapValues(
                    inputJson -> {
                        try {
                            return mapper.readValue(inputJson, SalesOrder.class);
                        } catch (Exception e) {
                            System.out.println("ERROR : Cannot convert JSON " + inputJson);
                            return null;
                        }
                    }
            );

            //Print objects received
            orderObjects.peek((key, value) -> System.out.println("Received Order : " + value));

            //Create a window of 5 seconds
            TimeWindows tumblingWindow = TimeWindows
                    .of(Duration.ofSeconds(5))
                    .grace(Duration.ZERO);

            //Initializer creates a new aggregator for every
            //Window & Product combination
            Initializer<OrderAggregator> orderAggregatorInitializer = OrderAggregator::new;

            //Aggregator - Compute total value and call the aggregator
            Aggregator<String, SalesOrder, OrderAggregator> orderAdder =
                    (key, value, aggregate) -> aggregate.add(value.getPrice() * value.getQuantity());

            //Perform Aggregation
            KTable<Windowed<String>, OrderAggregator> productSummary = orderObjects
                    .groupBy( //Group by Product
                            (key, value) -> value.getProduct(),
                            Grouped.with(stringSerde, orderSerde))
                    .windowedBy(tumblingWindow)
                    .aggregate(
                            orderAggregatorInitializer,
                            orderAdder,
                            //Store output in a materialized store
                            Materialized.<String, OrderAggregator,
                                            WindowStore<Bytes, byte[]>>as(
                                            "time-windowed-aggregate-store")
                                    .withValueSerde(aggregatorSerde))
                    .suppress(
                            Suppressed
                                    .untilWindowCloses(
                                            Suppressed.BufferConfig
                                                    .unbounded()
                                                    .shutDownWhenFull()));

            productSummary
                    .toStream() //convert KTable to KStream
                    .foreach((key, aggregation) ->
                            {
                                System.out.println("Received Summary :" +
                                        " Window = " + key.window().startTime() +
                                        " Product =" + key.key() +
                                        " Value = " + aggregation.getTotalValue());

                                //Write to order_summary table
                                dbUpdater.insertSummary(
                                        key.window().startTime().toString(),
                                        key.key(),
                                        aggregation.getTotalValue()
                                );
                            }
                    );

            /**************************************************
             * Create a pipe and execute
             **************************************************/
            //Create final topology and print
            Topology topology = builder.build();
            System.out.println(topology.describe());

            //Setup Stream
            KafkaStreams streams = new KafkaStreams(topology, properties);

            //Reset for the example. Not recommended for production
            streams.cleanUp();
            CountDownLatch latch = new CountDownLatch(1);

            // attach shutdown handler to catch control-c
            Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
                @Override
                public void run() {
                    System.out.println("Shutdown called..");
                    streams.close();
                    latch.countDown();
                }
            });

            //Start the stream
            streams.start();
            //Await termination
            latch.await();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
