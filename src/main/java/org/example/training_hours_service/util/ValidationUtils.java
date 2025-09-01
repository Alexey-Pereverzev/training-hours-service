package org.example.training_hours_service.util;

import org.example.training_hours_service.dto.EventType;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ValidationUtils {

    private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class.getName());
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static void validateTrainingUpdateRequest(TrainingUpdateRequest request) {
        if (request == null) {
            log.error("Validation failed: TrainingUpdateRequest is null");
            throw new IllegalArgumentException("Training update payload is required.");
        }
        if (request.getTrainerUsername() == null || request.getTrainerUsername().isBlank()) {
            log.error("Validation failed: Trainer username is missing");
            throw new IllegalArgumentException("Trainer username is required.");
        }
        if (request.getActionType() == null) {
            log.error("Validation failed: ActionType is missing for trainer '{}'", request.getTrainerUsername());
            throw new IllegalArgumentException("ActionType (ADD/DELETE) is required.");
        }
        if (request.getTrainingDuration() <= 0) {
            log.error("Validation failed: Training duration must be positive, trainer '{}'",
                    request.getTrainerUsername());
            throw new IllegalArgumentException("Training duration must be positive.");
        }
    }


    public static void validateTrainerHoursEvent(TrainerHoursEvent event) {
        if (event == null) {
            log.error("Validation failed: TrainerHoursEvent is null");
            throw new IllegalArgumentException("Event is required.");
        }
        if (event.getTxId() == null || event.getTxId().isBlank()) {
            log.error("Validation failed: txId is missing");
            throw new IllegalArgumentException("Transaction ID (txId) is required.");
        }
        if (event.getType() == null) {
            log.error("Validation failed: Event type is missing, txId={}", event.getTxId());
            throw new IllegalArgumentException("Event type is required.");
        }

        if (event.getType() == EventType.UPDATE) {
            validateTrainingUpdateRequest(event.getTrainingUpdate());
        }
    }


    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            log.error("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
    }
}