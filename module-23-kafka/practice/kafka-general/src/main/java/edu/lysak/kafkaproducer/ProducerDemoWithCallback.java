package edu.lysak.kafkaproducer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class ProducerDemoWithCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

    public static void main(String[] args) {
        String bootstrapServers = "127.0.0.1:9092";

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create safe producer (start)
//        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
//        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
//        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
//        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        // create safe producer (end)

        // high throughput producer (at the expense of a bit of latency and CPU usage) (start)
//        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
//        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
//        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(32 * 1024)); //32KB
        // high throughput producer (at the expense of a bit of latency and CPU usage) (end)


        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("second_topic", "Hello World! " + i);
            Callback callback = (recordMetadata, e) -> {
                if (e == null) {
                    LOGGER.info("Received new metadata:\ntopic = {};\npartition = {};\noffset = {};\ntimestamp = {}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), new Date(recordMetadata.timestamp()));
                } else {
                    LOGGER.error("Error during producing", e);
                }
            };
            producer.send(record, callback);
//            or
//            producer.send(record, new CustomCallback());
        }

        producer.flush(); //flush
        producer.close(); //flush and close
    }


    public static class CustomCallback implements Callback {
//        private final String messageKey;
//
//        //Set message key to identify message. Additional information
//        //can also be set here to provide context
//        public CustomCallback(String messageKey) {
//            this.messageKey = messageKey;
//        }

        @Override
        public void onCompletion(RecordMetadata retData, Exception e) {

            //Check if exception occured
            if (e != null) {
                System.out.println("Exception Publishing Asynchronously without Callback :"
                        + e.getMessage());
            } else {
                System.out.println(" Callback received:"
                        + " returned Partition : " + retData.partition()
                        + " and Offset : " + retData.offset());
            }
        }
    }
}
