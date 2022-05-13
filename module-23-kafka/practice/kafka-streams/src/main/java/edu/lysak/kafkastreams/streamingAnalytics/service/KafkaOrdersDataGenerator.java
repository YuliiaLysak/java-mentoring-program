package edu.lysak.kafkastreams.streamingAnalytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lysak.kafkastreams.streamingAnalytics.domain.SalesOrder;
import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/****************************************************************************
 * This Generator generates a a series of Orders into Kafka at random
 * intervals. This can be used to test Streaming Analytics pipelines
 ****************************************************************************/

public class KafkaOrdersDataGenerator implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        KafkaOrdersDataGenerator kodg = new KafkaOrdersDataGenerator();
        kodg.run();
    }

    public void run() {
        try {
            System.out.println("Starting Kafka Orders Generator..");
            //Wait for the main flow to be setup.
            Thread.sleep(5000);

            //Setup Kafka Client
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.SERVER);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            Producer<String, String> myProducer = new KafkaProducer<>(properties);

            //Define list of Products
            List<String> products = new ArrayList<>();
            products.add("Keyboard");
            products.add("Mouse");
            products.add("Monitor");

            //Define list of Prices. Matches the corresponding products
            List<Double> prices = new ArrayList<>();
            prices.add(25.00);
            prices.add(10.5);
            prices.add(140.00);

            //Define a random number generator
            Random random = new Random();

            ObjectMapper mapper = new ObjectMapper();

            //Capture current timestamp
            String currentTime = String.valueOf(System.currentTimeMillis());

            //Create order ID based on the timestamp
            int orderId = (int) Math.floor(System.currentTimeMillis() / 1000.0);

            //Generate 100 sample order records
            for (int i = 0; i < 100; i++) {
                SalesOrder order = new SalesOrder();
                order.setOrderId(orderId);
                orderId++;

                //Generate a random product
                int randomValue = random.nextInt(products.size());
                order.setProduct(products.get(randomValue));

                //Get product price
                order.setPrice(prices.get(randomValue));

                //Generate a random value for number of quantity
                order.setQuantity(random.nextInt(4) + 1);

                String key = String.valueOf(order.getOrderId());
                String value = mapper.writeValueAsString(order);

                //Create a Kafka producer record
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(
                                Constants.ORDERS_INPUT_TOPIC,
                                key,
                                value);

                RecordMetadata rmd = myProducer.send(record).get();

                System.out.println(ANSI_PURPLE +
                        "Kafka Orders Stream Generator : Sending Event : "
                        + String.join(",", value) + ANSI_RESET);

                //Sleep for a random time ( 1 - 3 secs) before the next record.
                Thread.sleep(random.nextInt(2000) + 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
