package edu.lysak.clientapp.service;

import edu.lysak.clientapp.repository.OrderRepository;
import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.Status;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerTest {

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    private Producer<Long, Notification> producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockBean
    private OrderRepository orderRepository;

    @BeforeAll
    void setUp() {
        producer = new DefaultKafkaProducerFactory<Long, Notification>(
                new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker)),
                new LongSerializer(),
                new JsonSerializer<>()
        ).createProducer();

        kafkaListenerEndpointRegistry.getListenerContainers()
                .forEach(
                        messageListenerContainer -> ContainerTestUtils
                                .waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic())
                );
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }

    @Test
    void listenOrderNotifications_shouldSuccessfullyListenNotification() {
        Notification notification = new Notification(1L, Status.PREPARING);
        producer.send(new ProducerRecord<>(notificationTopic, 1L, notification));
        producer.flush();

        verify(orderRepository, timeout(5000L))
                .updateOrderStatus(1L, Status.PREPARING);
    }
}
