package edu.lysak.kafkaproducer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoWithKeys {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithKeys.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String bootstrapServers = "127.0.0.1:9092";

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        for (int i = 0; i < 10; i++) {
            String topic = "second_topic";
            String value = "Hello World! " + i;
            String key = "id_" + i;
            LOGGER.info("KEY={}", key);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            Callback callback = (recordMetadata, e) -> {
                if (e == null) {
                    LOGGER.info("Received new metadata:\ntopic = {};\npartition = {};\noffset = {};\ntimestamp = {}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), new Date(recordMetadata.timestamp()));
                } else {
                    LOGGER.error("Error during producing", e);
                }
            };
            producer.send(record, callback).get(); // don't use .get() in production
        }

        producer.flush(); //flush
        producer.close(); //flush and close
    }
}
