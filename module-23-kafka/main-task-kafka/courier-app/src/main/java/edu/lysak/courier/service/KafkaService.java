package edu.lysak.courier.service;

import edu.lysak.domain.models.Notification;
import edu.lysak.domain.models.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
            topics = "${spring.kafka.topic.notification}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenFilteredNotifications(Notification notification) {
        Status status = notification.getStatus();
        Long orderId = notification.getOrderId();
        log.info("Received status={} for order with id={}", status, orderId);

        if (Status.PREPARED.equals(status)) {
            notification.setStatus(Status.DELIVERING);
            sendNotification(orderId, notification);
            return;
        }

        if (Status.DELIVERING.equals(status)) {
            deliverPizza();
            notification.setStatus(Status.COMPLETED);
            sendNotification(orderId, notification);
        }
    }

    private void deliverPizza() {
        try {
            // delivering pizza
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Long orderId, Notification notification) {
        ListenableFuture<SendResult<Long, Notification>> future = kafkaTemplate.send(
                notificationTopic,
                orderId,
                notification
        );
        future.addCallback(new ListenableFutureCallback<>() {
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
