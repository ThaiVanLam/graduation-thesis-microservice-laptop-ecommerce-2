package com.ecommerce.order_service.service;

import com.ecommerce.order_service.payload.EmailDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.notification.exchange}")
    private String notificationExchange;

    @Value("${queue.notification.routing-key}")
    private String notificationRoutingKey;

    public void sendEmailNotification(EmailDetails emailDetails) {
        rabbitTemplate.convertAndSend(notificationExchange, notificationRoutingKey, emailDetails);
    }
}