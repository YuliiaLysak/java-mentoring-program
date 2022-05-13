package edu.lysak.kafkastreams.simple;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class KafkaStreamsDemo {
    public static void main(String[] args) {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "demo-kafka-streams");
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        // create topology
        StreamsBuilder builder = new StreamsBuilder();

        // input topic
        KStream<String, String> inputTopic = builder.stream("second_topic");
        KStream<String, String> filteredStream = inputTopic.filter((k, v) -> v.contains("3"));
        filteredStream.to("filtered_topic");

        // build topology
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);

        // start streams app
        kafkaStreams.start();
    }
}
