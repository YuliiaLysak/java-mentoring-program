package edu.lysak.kafkatwitter;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class ElasticSearchConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConsumer.class);

    private static final String BOOTSTRAP_SERVERS = "127.0.0.1:9092";
    private static final String GROUP_ID = "twitter-consumer-group";
    private static final String TOPIC = "twitter_tweets";

    // Auto commit of offsets
    /*
    public static void main(String[] args) throws IOException, InterruptedException {
        RestHighLevelClient client = createClient();
        KafkaConsumer<String, String> consumer = createConsumer(TOPIC);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

//                  kafka generic id
//                String id = String.format("%s_%s_%s", record.topic(), record.partition(), record.offset());
//                or
//                tweets specific id
                String id = extractIdFromJson(record.value());

                IndexRequest request = new IndexRequest(
                        "twitter",
                        "tweets",
                        id // this is to make consumer idempotent (not read the same data twice or more)
                ).source(record.value(), XContentType.JSON);
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                LOGGER.info(response.getId());
                Thread.sleep(1000); // just for slow displaying read data in console
            }
        }

//        client.close();
    }

     */

    // Manual commit of offsets
    public static void main(String[] args) throws InterruptedException, IOException {
        RestHighLevelClient client = createClient();
        KafkaConsumer<String, String> consumer = createConsumerForManualOffsetCommit(TOPIC);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            LOGGER.info("Received {} records", records.count());

            BulkRequest bulkRequest = new BulkRequest();
            for (ConsumerRecord<String, String> record : records) {
                try {
                    String id = extractIdFromJson(record.value());
                    IndexRequest request = new IndexRequest(
                            "twitter",
                            "tweets",
                            id // this is to make consumer idempotent (not read the same data twice or more)
                    ).source(record.value(), XContentType.JSON);
                    bulkRequest.add(request);
                } catch (Exception e) {
                    LOGGER.warn("Exception occurs '{}', skipping bad data = '{}'", e.getMessage(), record.value());
                }
            }

            if (records.count() > 0) {
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                LOGGER.info("Committing offsets...");
                consumer.commitSync();
                LOGGER.info("Offsets have been commited");
                Thread.sleep(1000);
            }
        }
    }

    private static String extractIdFromJson(String jsonValue) {
        return JsonParser.parseString(jsonValue)
                .getAsJsonObject()
                .get("id_str")
                .getAsString();
    }

    public static RestHighLevelClient createClient() {
        String hostname = "localhost";
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 9200, "http"));
        return new RestHighLevelClient(builder);
    }

    public static KafkaConsumer<String, String> createConsumer(String topic) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(properties);
    }

    public static KafkaConsumer<String, String> createConsumerForManualOffsetCommit(String topic) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable autocommit of offsets
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");

        return new KafkaConsumer<>(properties);
    }
}
