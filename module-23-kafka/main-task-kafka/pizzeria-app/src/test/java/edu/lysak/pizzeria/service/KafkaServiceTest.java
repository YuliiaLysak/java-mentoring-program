package edu.lysak.pizzeria.service;

import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.OrderInfo;
import edu.lysak.domain.models.Status;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EmbeddedKafka
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaServiceTest {

    private Producer<Long, OrderInfo> producer;

    @Value("${spring.kafka.topic.order}")
    private String orderTopic;
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
        producer = new DefaultKafkaProducerFactory<Long, OrderInfo>(
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

    @Test
    void listenToOrders() throws InterruptedException {
        SettableListenableFuture<SendResult<Long, Notification>> future = new SettableListenableFuture<>();
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        OrderInfo orderInfo = new OrderInfo(
                1L,
                "small",
                Set.of("cheese", "pepperoni", "mushrooms"),
                Status.ORDER_PLACED);
        producer.send(new ProducerRecord<>(orderTopic, 1L, orderInfo));
        producer.flush();

        Thread.sleep(20000);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(kafkaTemplate, times(2))
                .send(eq(notificationTopic), eq(1L), notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertEquals(Status.PREPARED, notification.getStatus());
    }
}
