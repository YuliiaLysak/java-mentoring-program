package edu.lysak.kafkastreams.alertsAndTresholds;

import edu.lysak.kafkastreams.util.ClassDeSerializer;
import edu.lysak.kafkastreams.util.ClassSerializer;
import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.Windowed;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/****************************************************************************
 * This is an example for Alerts & Thresholds in Kafka Streams.
 * It reads a real time alerts stream from kafka
 * compares against thresholds and publishes exceptions
 ****************************************************************************/
public class StreamingThresholdsAndAlerts {

    public static void main(String[] args) {

        //Initiate the Kafka Alerts Generator
        KafkaAlertsDataGenerator alertsGenerator = new KafkaAlertsDataGenerator();
        Thread genThread = new Thread(alertsGenerator);
        genThread.start();

        System.out.println("******** Starting Streaming  *************");

        try {
            /**************************************************
             * Build a Kafka Topology
             **************************************************/

            //Setup Serializer / DeSerializer for used Data types
            Serde<String> stringSerde = Serdes.String();
            Serde<Long> longSerde = Serdes.Long();
            Serde<Alert> alertSerde = Serdes.serdeFrom(
                    new ClassSerializer<>(),
                    new ClassDeSerializer<>(Alert.class)
            );

            //Setup Properties for the Kafka Input Stream
            Properties properties = new Properties();
            properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "alerts-and-thresholds-pipe");
            properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.SERVER);
            properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //For immediate results during testing
            properties.setProperty(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
            properties.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100");

            //Initiate the Kafka Streams Builder
            StreamsBuilder builder = new StreamsBuilder();

            //Create the source node for Alerts
            KStream<String, String> alertInput = builder.stream(
                    Constants.ALERTS_INPUT_TOPIC,
                    Consumed.with(stringSerde, stringSerde)
            );

            //Convert value to an Alert Object
            KStream<String, Alert> alertObject = alertInput.mapValues((inputCSV) -> {
                String[] values = inputCSV
                        .replaceAll("\"", "")
                        .split(",");
                Alert alert1 = new Alert();
                alert1.setTimestamp(Timestamp.valueOf(values[0]));
                alert1.setLevel(values[1]);
                alert1.setCode(values[2]);
                alert1.setMsg(values[3]);
                System.out.println("Received Alert :" + alert1);
                return alert1;
            });

            //Filter Critical Alerts and Publish to an outgoing topic
            alertObject
                    .filter((key, alert) -> alert.getLevel().equals("CRITICAL"))
                    .mapValues(alert ->
                            "\"" + alert.getTimestamp() + "\"," +
                            "\"" + alert.getCode() + "\"," +
                            "\"" + alert.getMsg() + "\"")
                    //Publish to outgoing topic
                    .to(Constants.ALERTS_CRITICAL_TOPIC);


            //Create a tumbling window of 10 seconds
            TimeWindows tumblingWindow = TimeWindows
                    .of(Duration.ofSeconds(10))
                    .grace(Duration.ZERO);

            //Aggregate by Code and window
            KTable<Windowed<String>, Long> codeCounts = alertObject
                    .groupBy( //Group by Code
                            (key, value) -> value.getCode(),
                            Grouped.with(stringSerde, alertSerde))
                    .windowedBy(tumblingWindow)
                    .count(Materialized.as("code-counts")) //Count Records
                    .suppress(
                            Suppressed
                                    .untilWindowCloses(
                                            Suppressed.BufferConfig
                                                    .unbounded()
                                                    .shutDownWhenFull()));


            codeCounts
                    .toStream()
                    .peek((key, value) ->
                            System.out.println("Summary record :" + key + " = " + value))
                    .filter((key, value) //Filter for high volume alerts
                            -> value > 2)
                    .map(  //Convert key and value to String for publishing
                            new KeyValueMapper<Windowed<String>,
                                    Long, KeyValue<String, String>>() {
                                @Override
                                public KeyValue<String, String>
                                apply(Windowed<String> key, Long value) {

                                    String returnKey = key.toString();
                                    String returnVal = "\"" + key.window().startTime() + "\"," +
                                            "\"" + key.key() + "\"," +
                                            "\"" + value.toString() + "\"";

                                    System.out.println("High Volume Alert : "
                                            + returnVal);
                                    return new KeyValue<>(returnKey, returnVal);
                                }
                            }
                    )
                    //Publish to outgoing topic.
                    .to(Constants.ALERTS_HIGHVOLUME_TOPIC);

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
