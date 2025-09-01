package org.example.training_hours_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.training_hours_service.config.JmsConfig;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.service.TrainerMonthlyHoursService;
import org.example.training_hours_service.util.ValidationUtils;
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
        try {                                                           // request processing
            ValidationUtils.validateTrainerHoursEvent(event);           // checking necessary fields
            switch (event.getType()) {
                case UPDATE -> {
                    service.updateTrainerHours(event.getTrainingUpdate());
                    log.info("Hours updated, txId={}", event.getTxId());
                }
                case CLEAR_ALL -> {
                    service.clearAll();
                    log.info("All hours cleared, txId={}", event.getTxId());
                }
            }
        } catch (DataAccessException e) {                               // DB problem -> will be retried, then DLQ
            log.error("Data access error, txId={}", event.getTxId(), e);
            throw e;
        } catch (IllegalArgumentException e) {                          // wrong data -> DLQ
            throw e;                                                    // no need to log - logged above
        } catch (Exception e) {                                         // unexpected error → DLQ
            log.error("Unexpected error, txId={}", event.getTxId(), e);
            throw new IllegalStateException("Unexpected fatal error, txId=" + event.getTxId(), e);
        }
    }

}
