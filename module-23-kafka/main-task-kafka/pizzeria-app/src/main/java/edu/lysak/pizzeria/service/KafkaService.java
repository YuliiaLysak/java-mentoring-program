package edu.lysak.pizzeria.service;

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

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaService {
    private final KafkaTemplate<Long, Notification> kafkaTemplate;
    private final String notificationTopic;


    public KafkaService(
            KafkaTemplate<Long, Notification> kafkaTemplate,
            @Value("${spring.kafka.topic.notification}") String notificationTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationTopic = notificationTopic;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.order}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenToOrders(OrderInfo orderInfo) {
        CompletableFuture.runAsync(() -> {
            Status status = orderInfo.getStatus();
            Long orderId = orderInfo.getOrderId();
            log.info("Received status={} for order with id={}", status, orderId);
            Notification notification = Notification.builder()
                    .orderId(orderId)
                    .status(Status.PREPARING)
                    .build();

            sendNotification(orderId, notification);
            preparePizza();
            notification.setStatus(Status.PREPARED);
            sendNotification(orderId, notification);
        });
    }

    private void preparePizza() {
        try {
            // preparing pizza
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Long orderId, Notification notification) {
        kafkaTemplate
                .send(notificationTopic, orderId, notification)
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onSuccess(SendResult<Long, Notification> result) {
                        log.info("Sent notification with status={} for orderId={}", notification.getStatus(), orderId);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Unable to send notification for orderId={} due to : {}", orderId, ex.getMessage(), ex);
                    }
                });
    }
}
