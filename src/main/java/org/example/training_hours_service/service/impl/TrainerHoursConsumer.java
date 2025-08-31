package org.example.training_hours_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.training_hours_service.config.JmsConfig;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.service.TrainerMonthlyHoursService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TrainerHoursConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrainerHoursConsumer.class);
    private final TrainerMonthlyHoursService service;


    @JmsListener(destination = JmsConfig.TRAINING_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void handle(TrainerHoursEvent event, @Header(name = "JMSXDeliveryCount", required = false) Integer deliveryCount) {
        log.info("Received event: event={}, deliveryCount={}", event, deliveryCount);

        if (event.getTxId() == null) {                  // checking necessary fields
            log.warn("Invalid event: missing txId");
            throw new IllegalArgumentException("Missing txId");
        }
        if (event.getType() == null) {
            log.warn("Invalid event: missing type, txId={}", event.getTxId());
            throw new IllegalArgumentException("Missing type, txId=" + event.getTxId());
        }

        try {                                           //  request processing
            switch (event.getType()) {
                case UPDATE -> {
                    if (event.getTrainingUpdate() == null) {
                        log.warn("Invalid UPDATE: missing payload, txId={}", event.getTxId());
                        throw new IllegalArgumentException("Missing payload for UPDATE, txId=" + event.getTxId());
                    }
                    if (event.getTrainingUpdate().getTrainerUsername() == null) {
                        log.warn("Invalid UPDATE: missing trainerUsername, txId={}", event.getTxId());
                        throw new IllegalArgumentException("Missing trainerUsername in TrainingUpdateRequest, txId="
                                + event.getTxId());
                    }
                    service.updateTrainerHours(event.getTrainingUpdate());
                    log.info("Hours updated, txId={}", event.getTxId());
                }
                case CLEAR_ALL -> {
                    service.clearAll();
                    log.info("All hours cleared, txId={}", event.getTxId());
                }
            }
        } catch (DataAccessException e) {           // DB problem -> will be retried, then DLQ
            log.error("Data access error, txId={}", event.getTxId(), e);
            throw e;
        } catch (IllegalArgumentException e) {      // wrong data -> DLQ
            throw e;                                // no need to log - logged above
        } catch (Exception e) {                     // unexpected error → DLQ
            log.error("Unexpected error, txId={}", event.getTxId(), e);
            throw new IllegalStateException("Unexpected fatal error, txId=" + event.getTxId(), e);
        }
    }

}

