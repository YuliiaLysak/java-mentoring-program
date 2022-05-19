package edu.lysak.clientapp.service;

import edu.lysak.clientapp.repository.OrderRepository;
import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.OrderInfo;
import edu.lysak.domain.models.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<Long, OrderInfo> kafkaTemplate;
    private final String orderTopic;


    public KafkaService(
            OrderRepository orderRepository,
            KafkaTemplate<Long, OrderInfo> kafkaTemplate,
            @Value("${spring.kafka.topic.order}") String orderTopic
    ) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.orderTopic = orderTopic;
    }

    public void sendOrder(Long orderId, OrderInfo orderInfo) {
        kafkaTemplate.send(orderTopic, orderId, orderInfo)
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onSuccess(SendResult<Long, OrderInfo> result) {
                        log.info("Sent order with id={}", orderId);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Unable to send order with id={} due to : {}", orderId, ex.getMessage(), ex);
                    }
                });
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.notification}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenOrderNotifications(Notification notification) {
//    public void listenOrderNotifications(ConsumerRecord<String, Notification> record) {
//        String key = record.key();
//        Notification value = record.value();
        Status status = notification.getStatus();
        Long orderId = notification.getOrderId();
        log.info("Received status={} for order with id={}", status, orderId);
        orderRepository.updateOrderStatus(orderId, status);
    }
}
