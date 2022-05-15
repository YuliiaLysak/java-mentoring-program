package edu.lysak.courier.config;

import edu.lysak.domain.models.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/*
@EnableKafka annotation is required on the configuration class
to enable detection of @KafkaListener annotation on spring-managed beans
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<Long, Notification> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.consumer.group-id}") String consumersGroupId
    ) {
        // because of exception "The class 'Notification' is not in the trusted packages"
        JsonDeserializer<Notification> deserializer = new JsonDeserializer<>(Notification.class);
        deserializer.addTrustedPackages("*");
        //

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumersGroupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, Notification> kafkaListenerContainerFactory(
            ConsumerFactory<Long, Notification> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Long, Notification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
//        factory.setRecordFilterStrategy(record -> Status.PREPARED.equals(record.value().getStatus())
//        || Status.DELIVERING.equals(record.value().getStatus()));
        factory.setConcurrency(3);
        return factory;
    }
}
