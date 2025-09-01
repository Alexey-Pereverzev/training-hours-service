package org.example.training_hours_service.service.impl;

import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;


@Service
public class DeadLetterConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @JmsListener(destination = "DLQ.training.events.queue", containerFactory = "jmsListenerContainerFactory")
    public void handleDeadLetter(Message message) {
        MDC.put("queue", "DLQ");
        try {
            String body = message.getBody(String.class);
            String destination = message.getJMSDestination().toString();
            log.warn("Received from DLQ [{}]: {}", destination, body);
        } catch (Exception e) {
            log.warn("Failed to read DLQ message", e);
        } finally {
            MDC.clear();
        }
    }
}

