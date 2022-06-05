package edu.lysak.courier.service;

import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.Status;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaServiceTest {

    private Producer<Long, Notification> producer;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockBean
    private KafkaTemplate<Long, Notification> kafkaTemplate;

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
    void tearDown() {
        producer.close();
    }

    @ParameterizedTest
    @ArgumentsSource(KafkaServiceTestData.ValidOrderStatus.class)
    void listenFilteredNotifications_shouldSuccessfullyListenNotification_andSendItToKafkaTopic(
            Status receivedStatus,
            Status sentStatus
    ) {
        Notification notification = new Notification(1L, receivedStatus);
        producer.send(new ProducerRecord<>(notificationTopic, 1L, notification));
        producer.flush();

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(kafkaTemplate, timeout(15000L))
                .send(eq(notificationTopic), eq(1L), notificationCaptor.capture());

        Notification receivedNotification = notificationCaptor.getValue();
        assertEquals(sentStatus, receivedNotification.getStatus());
    }

    @ParameterizedTest
    @ArgumentsSource(KafkaServiceTestData.IgnoredOrderStatus.class)
    void listenFilteredNotifications_ifNotificationIsIgnored_shouldNotSendItToKafkaTopic(Status receivedStatus) throws InterruptedException {
        Notification notification = new Notification(1L, receivedStatus);
        producer.send(new ProducerRecord<>(notificationTopic, 1L, notification));
        producer.flush();

        Thread.sleep(5000);
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

}
