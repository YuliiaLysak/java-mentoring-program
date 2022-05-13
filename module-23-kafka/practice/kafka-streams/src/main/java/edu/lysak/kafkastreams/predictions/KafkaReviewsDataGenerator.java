package edu.lysak.kafkastreams.predictions;

import edu.lysak.kafkastreams.util.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

/****************************************************************************
 * This Generator generates a a series movie review
 * into Kafka at random intervals
 * This can be used to test real time prediction pipelines
 ****************************************************************************/

public class KafkaReviewsDataGenerator implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        KafkaReviewsDataGenerator kodg = new KafkaReviewsDataGenerator();
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

            Producer<String, String> myProducer
                    = new KafkaProducer<>(properties);

            //Define a random number generator
            Random random = new Random();

            //Get reviews from the movie-reviews.txt file
            Scanner scanner = new Scanner(new File("module-23-kafka/practice/kafka-streams/src/main/resources/movie-reviews.txt"));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                int reviewId = (int) Math.floor(System.currentTimeMillis() / 1000.0);

                //Publish the review
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        Constants.SENTIMENT_INPUT_TOPIC,
                        String.valueOf(reviewId),
                        line
                );

                RecordMetadata rmd = myProducer.send(record).get();

                System.out.println(ANSI_PURPLE +
                        "Kafka Reviews Stream Generator : Sending Event : "
                        + reviewId + " = " + line + ANSI_RESET);

                //Sleep for a random time ( 1 - 3 secs) before the next record.
                Thread.sleep(random.nextInt(2000) + 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
