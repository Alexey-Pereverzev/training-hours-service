package org.example.training_hours_service.util;

import org.example.training_hours_service.dto.ActionType;
import org.example.training_hours_service.dto.EventType;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class ValidationUtilsTest {

    @Test
    void whenValidateTrainingUpdateRequest_null_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainingUpdateRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training update payload is required.");
    }


    @Test
    void whenValidateTrainingUpdateRequest_blankTrainer_shouldThrow() {
        // given
        TrainingUpdateRequest req = TrainingUpdateRequest.builder()
                .trainerUsername(" ")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainingUpdateRequest(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer username is required.");
    }


    @Test
    void whenValidateTrainingUpdateRequest_missingActionType_shouldThrow() {
        // given
        TrainingUpdateRequest req = TrainingUpdateRequest.builder()
                .trainerUsername("trainer1")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainingUpdateRequest(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ActionType (ADD/DELETE) is required.");
    }


    @Test
    void whenValidateTrainingUpdateRequest_nonPositiveDuration_shouldThrow() {
        // given
        TrainingUpdateRequest req = TrainingUpdateRequest.builder()
                .trainerUsername("trainer1")
                .trainingDate(LocalDate.now())
                .trainingDuration(0)
                .actionType(ActionType.ADD)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainingUpdateRequest(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training duration must be positive.");
    }


    @Test
    void whenValidateTrainingUpdateRequest_valid_ok() {
        // given
        TrainingUpdateRequest req = TrainingUpdateRequest.builder()
                .trainerUsername("trainer1")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        // when + then
        assertDoesNotThrow(() -> ValidationUtils.validateTrainingUpdateRequest(req));
    }


    @Test
    void whenValidateTrainerHoursEvent_null_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainerHoursEvent(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event is required.");
    }


    @Test
    void whenValidateTrainerHoursEvent_missingTxId_shouldThrow() {
        // given
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .type(EventType.UPDATE)
                .trainingUpdate(TrainingUpdateRequest.builder()
                        .trainerUsername("trainer1")
                        .trainingDate(LocalDate.now())
                        .trainingDuration(60)
                        .actionType(ActionType.ADD)
                        .build())
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainerHoursEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction ID (txId) is required.");
    }


    @Test
    void whenValidateTrainerHoursEvent_missingType_shouldThrow() {
        // given
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId("123")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainerHoursEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event type is required.");
    }


    @Test
    void whenValidateTrainerHoursEvent_updateWithoutTrainingUpdate_shouldThrow() {
        // given
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId("123")
                .type(EventType.UPDATE)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainerHoursEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training update payload is required.");
    }


    @Test
    void whenValidateTrainerHoursEvent_clearAll_ok() {
        // given
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId("123")
                .type(EventType.CLEAR_ALL)
                .build();
        // when + then
        assertDoesNotThrow(() -> ValidationUtils.validateTrainerHoursEvent(event));
    }


    @Test
    void whenValidateUsername_blank_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateUsername(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is required.");
    }


    @Test
    void whenValidateUsername_valid_ok() {
        // when + then
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("Good.Username"));
    }


    @Test
    void constructor_shouldBePrivateAndThrow() throws Exception {
        // given
        var constructor = ValidationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // when + then
        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("Utility class");
    }
}


