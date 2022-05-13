package edu.lysak.kafkastreams.websiteViews;

import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/****************************************************************************
 * This Generator generates a webpage view events
 * into Kafka at random intervals
 * This can be used to test Streaming pipelines
 ****************************************************************************/

public class KafkaViewsDataGenerator implements Runnable {


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        KafkaViewsDataGenerator kodg = new KafkaViewsDataGenerator();
        kodg.run();
    }

    public void run() {

        try {

            System.out.println("Starting Kafka Movie review Generator..");
            //Wait for the main flow to be setup.
            Thread.sleep(5000);

            //Setup Kafka Client
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.SERVER);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            Producer<String, String> myProducer = new KafkaProducer<>(properties);

            //Define list of Products
            List<String> users = List.of("Bob", "Mike", "Kathy", "Sam");

            List<String> topics = List.of("AI", "BigData", "CI_CD", "Cloud");

            //Define a random number generator
            Random random = new Random();

            int key = (int) Math.floor(System.currentTimeMillis() / 1000.0);

            //Generate 100 sample order records
            for (int i = 0; i < 100; i++) {

                key++;

                //Capture current timestamp
                Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
                String user = users.get(random.nextInt(users.size()));
                String topic = topics.get(random.nextInt(topics.size()));
                int minutes = random.nextInt(10) + 1;

                //Form a CSV
                String value = "\"" + currTimeStamp + "\","
                        + "\"" + user + "\","
                        + "\"" + topic + "\","
                        + minutes;


                ProducerRecord<String, String> record = new ProducerRecord<>(
                        Constants.VIEWS_INPUT_TOPIC,
                        String.valueOf(key),
                        value
                );

                RecordMetadata rmd = myProducer.send(record).get();

                System.out.println(ANSI_PURPLE +
                        "Kafka Views Stream Generator : Sending Event : "
                        + String.join(",", value) + ANSI_RESET);

                //Sleep for a random time ( 1 - 3 secs) before the next record.
                Thread.sleep(random.nextInt(2000) + 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
