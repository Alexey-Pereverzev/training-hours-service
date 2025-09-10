package org.example.training_hours_service.service.impl;

import org.example.training_hours_service.dto.ActionType;
import org.example.training_hours_service.dto.EventType;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.example.training_hours_service.service.TrainerMonthlyHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class TrainerHoursConsumerTest {

    @Mock
    private TrainerMonthlyHoursService service;

    @InjectMocks
    private TrainerHoursConsumer consumer;

    private TrainerHoursEvent event;

    @BeforeEach
    void setUp() {
        event = TrainerHoursEvent.builder()
                .txId("123")
                .type(EventType.UPDATE)
                .trainingUpdate(TrainingUpdateRequest.builder()
                        .trainerUsername("trainer1")
                        .trainingDuration(90)
                        .actionType(ActionType.ADD)
                        .build())
                .build();
    }


    @Test
    void whenHandle_updateEvent_shouldCallUpdateTrainerHours() {
        // when
        consumer.handle(event, 1);
        // then
        verify(service).updateTrainerHours(event.getTrainingUpdate());
        verify(service, never()).clearAll();
    }


    @Test
    void whenHandle_clearAllEvent_shouldCallClearAll() {
        // given
        event = TrainerHoursEvent.builder()
                .txId("456")
                .type(EventType.CLEAR_ALL)
                .build();
        // when
        consumer.handle(event, 2);
        // then
        verify(service).clearAll();
        verify(service, never()).updateTrainerHours(any());
    }


    @Test
    void whenHandle_serviceThrowsDataAccessException_shouldPropagate() {
        // given
        doThrow(new DataAccessResourceFailureException("db down")).when(service).updateTrainerHours(any());
        // when + then
        assertThrows(DataAccessResourceFailureException.class, () -> consumer.handle(event, 1));
    }


    @Test
    void whenHandle_validationFails_shouldThrowIllegalArgumentException() {
        // given
        TrainerHoursEvent badEvent = TrainerHoursEvent.builder()
                .txId("789")
                .type(EventType.UPDATE)
                .build();                           // trainingUpdate == null
        // when + then
        assertThrows(IllegalArgumentException.class, () -> consumer.handle(badEvent, 1));
    }


    @Test
    void whenHandle_unexpectedException_shouldThrowIllegalStateException() {
        // given
        doThrow(new RuntimeException("boom")).when(service).updateTrainerHours(any());
        // when + then
        assertThrows(IllegalStateException.class, () -> consumer.handle(event, 1));
    }
}

