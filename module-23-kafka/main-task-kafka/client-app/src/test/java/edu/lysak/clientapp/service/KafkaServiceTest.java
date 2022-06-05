package edu.lysak.clientapp.service;

import edu.lysak.clientapp.repository.OrderRepository;
import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.OrderInfo;
import edu.lysak.domain.models.Status;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaServiceTest {

    private BlockingQueue<ConsumerRecord<Long, OrderInfo>> consumerRecords;
    private KafkaMessageListenerContainer<Long, OrderInfo> container;
    private Producer<Long, Notification> producer;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;
    @Value("${spring.kafka.topic.order}")
    private String orderTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaService kafkaService;

    @MockBean
    private OrderRepository orderRepository;

    @BeforeAll
    void setUp() {
        JsonDeserializer<OrderInfo> deserializer = new JsonDeserializer<>(OrderInfo.class);
        deserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<Long, OrderInfo> consumerFactory = new DefaultKafkaConsumerFactory<>(
                getConsumerProperties(deserializer),
                new LongDeserializer(),
                deserializer
        );
        ContainerProperties containerProperties = new ContainerProperties(orderTopic, notificationTopic);

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        consumerRecords = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<Long, OrderInfo>) consumerRecords::add);
        container.start();
        producer = new DefaultKafkaProducerFactory<Long, Notification>(
                new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker)),
                new LongSerializer(),
                new JsonSerializer<>()
        ).createProducer();

        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        container.stop();
        producer.close();
    }

    @Test
    void sendOrder_shouldSuccessfullySendOrderToTopic() throws InterruptedException {
        // Create record and write to Kafka
        kafkaService.sendOrder(1L, new OrderInfo(
                1L,
                "small",
                Set.of("cheese", "pepperoni", "mushrooms"),
                Status.ORDER_PLACED));

        // Read the record with a test consumer from Kafka and assert its properties
        ConsumerRecord<Long, OrderInfo> orderRecord = consumerRecords.poll(5000, TimeUnit.MILLISECONDS);
        assertNotNull(orderRecord);
        assertEquals(1L, orderRecord.key());

        OrderInfo orderValue = orderRecord.value();
        assertNotNull(orderValue);
        assertEquals(1L, orderValue.getOrderId());
        assertEquals("small", orderValue.getPizzaSize());
        assertEquals(3, orderValue.getToppings().size());
        assertEquals(Status.ORDER_PLACED, orderValue.getStatus());
    }

    @Test
    void listenOrderNotifications_shouldSuccessfullyListenNotification() {
        // Create record and send it to Kafka with test producer
        Notification notification = new Notification(1L, Status.PREPARING);
        producer.send(new ProducerRecord<>(notificationTopic, 1L, notification));
        producer.flush();

        // Verify receiving record using @KafkaListener and updating database
        verify(orderRepository, timeout(5000L))
                .updateOrderStatus(1L, Status.PREPARING);
    }

    private Map<String, Object> getConsumerProperties(JsonDeserializer<OrderInfo> deserializer) {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
        );
    }
}
