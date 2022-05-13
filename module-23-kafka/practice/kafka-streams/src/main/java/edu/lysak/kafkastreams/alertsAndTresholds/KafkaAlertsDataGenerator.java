package edu.lysak.kafkastreams.alertsAndTresholds;

import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/****************************************************************************
 * This Generator generates a a series of Exception logs into Kafka at random
 * intervals. This can be used to test Alerts and Thresholds pipelines
 ****************************************************************************/

public class KafkaAlertsDataGenerator implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        KafkaAlertsDataGenerator kodg = new KafkaAlertsDataGenerator();
        kodg.run();
    }

    public void run() {
        try {
            System.out.println("Starting Kafka Alerts Generator..");
            //Wait for the main flow to be setup.
            Thread.sleep(5000);

            //Setup Kafka Client
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.SERVER);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            Producer<String, String> myProducer = new KafkaProducer<>(properties);

            //Define list of Exception Levels
            List<String> levels = List.of("CRITICAL", "HIGH", "ELEVATED");

            //Define list of exception codes
            List<String> codes = List.of("100", "200", "300", "400");

            //Define a random number generator
            Random random = new Random();

            //Create a record key using the system timestamp.
            int key = (int) Math.floor(System.currentTimeMillis() / 1000.0);

            //Generate 100 sample exception messages
            for (int i = 0; i < 100; i++) {
                key++;

                //Capture current timestamp
                Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis());
                //Get a random Exception Level
                String thisLevel = levels.get(random.nextInt(levels.size()));
                //Get a random Exception code
                String thisCode = codes.get(random.nextInt(codes.size()));

                //Form a CSV. Use a dummy exception message
                String value = "\"" + currTimeStamp + "\","
                        + "\"" + thisLevel + "\","
                        + "\"" + thisCode + "\","
                        + "\"This is a " + thisLevel + " alert\"";

                //Create the producer record
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(
                                Constants.ALERTS_INPUT_TOPIC,
                                String.valueOf(key),
                                value);

                //Send data to Kafka
                RecordMetadata rmd = myProducer.send(record).get();

                System.out.println(ANSI_PURPLE +
                        "Kafka Orders Stream Generator : Sending Event : "
                        + String.join(",", value) + ANSI_RESET);

                //Sleep for a random time ( 1 - 2 secs) before the next record.
                Thread.sleep(random.nextInt(1000) + 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
