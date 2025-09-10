package org.example.training_hours_service.service.impl;

import org.example.training_hours_service.dto.ActionType;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.example.training_hours_service.entity.MonthSummary;
import org.example.training_hours_service.entity.TrainerMonthlyHours;
import org.example.training_hours_service.repository.TrainerMonthlyHoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerMonthlyHoursServiceImplTest {

    @Mock
    private TrainerMonthlyHoursRepository repository;

    @InjectMocks
    private TrainerMonthlyHoursServiceImpl service;

    private TrainingUpdateRequest request;

    @BeforeEach
    void setUp() {
        request = TrainingUpdateRequest.builder()
                .trainerUsername("Nina.Petrova")
                .trainerFirstName("Nina")
                .trainerLastName("Petrova")
                .active(true)
                .trainingDate(LocalDate.of(2025, 1, 15))
                .trainingDuration(120)              //  2h
                .actionType(ActionType.ADD)
                .build();
    }


    @Test
    void whenUpdateTrainerHours_addNewEntry_shouldSaveTrainer() {
        // given
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.empty());
        // when
        service.updateTrainerHours(request);
        // then
        ArgumentCaptor<TrainerMonthlyHours> captor = ArgumentCaptor.forClass(TrainerMonthlyHours.class);
        verify(repository).save(captor.capture());
        TrainerMonthlyHours saved = captor.getValue();
        assertEquals("Nina.Petrova", saved.getUsername());
        assertEquals("Nina", saved.getFirstName());
        assertEquals(2.0, saved.getMonthlyHoursByYear().get(2025).getFirst().getTotalHours());
    }


    @Test
    void whenUpdateTrainerHours_deleteExistingHours_shouldDecrease() {
        // given
        MonthSummary summary = new MonthSummary(1, 5.0); // было 5 часов
        Map<Integer, List<MonthSummary>> hours = new HashMap<>();
        hours.put(2025, new ArrayList<>(List.of(summary)));
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .firstName("Nina")
                .lastName("Petrova")
                .active(true)
                .monthlyHoursByYear(hours)
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(120); // минус 2 часа
        // when
        service.updateTrainerHours(request);
        // then
        ArgumentCaptor<TrainerMonthlyHours> captor = ArgumentCaptor.forClass(TrainerMonthlyHours.class);
        verify(repository).save(captor.capture());
        assertEquals(3.0, captor.getValue().getMonthlyHoursByYear().get(2025).getFirst().getTotalHours());
    }


    @Test
    void whenGetHoursForTrainerInMonth_noTrainer_shouldReturnZero() {
        // given
        when(repository.findById("unknown")).thenReturn(Optional.empty());
        // when
        double hours = service.getHoursForTrainerInMonth("unknown", 2025, 1);
        // then
        assertEquals(0.0, hours);
    }


    @Test
    void whenGetHoursForTrainerInMonth_existingTrainer_shouldReturnHours() {
        // given
        MonthSummary summary = new MonthSummary(1, 7.5);
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(Map.of(2025, List.of(summary)))
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        // when
        double hours = service.getHoursForTrainerInMonth("Nina.Petrova", 2025, 1);
        // then
        assertEquals(7.5, hours);
    }


    @Test
    void whenClearAll_shouldInvokeDeleteAllOnRepository() {
        // when
        service.clearAll();
        // then
        verify(repository).deleteAll();
    }


    @Test
    void whenUpdateTrainerHours_addToExistingMonth_shouldIncrease() {
        // given
        MonthSummary summary = new MonthSummary(1, 2.0); // было 2 часа
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(new HashMap<>(Map.of(2025, new ArrayList<>(List.of(summary)))))
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        // ADD 1 час
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
        // when
        service.updateTrainerHours(request);
        // then
        ArgumentCaptor<TrainerMonthlyHours> captor = ArgumentCaptor.forClass(TrainerMonthlyHours.class);
        verify(repository).save(captor.capture());
        assertEquals(3.0, captor.getValue().getMonthlyHoursByYear().get(2025).getFirst().getTotalHours());
    }


    @Test
    void whenUpdateTrainerHours_deleteMoreThanAvailable_shouldResetToZero() {
        // given
        MonthSummary summary = new MonthSummary(1, 1.0); // было 1 час
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(new HashMap<>(Map.of(2025, new ArrayList<>(List.of(summary)))))
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(120); // пытаемся удалить 2 часа
        // when
        service.updateTrainerHours(request);
        // then
        ArgumentCaptor<TrainerMonthlyHours> captor = ArgumentCaptor.forClass(TrainerMonthlyHours.class);
        verify(repository).save(captor.capture());
        assertEquals(0.0, captor.getValue().getMonthlyHoursByYear().get(2025).getFirst().getTotalHours());
    }


    @Test
    void whenUpdateTrainerHours_deleteFromNonExistingMonth_shouldNotCreateMonth() {
        // given
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(new HashMap<>(Map.of(2025, new ArrayList<>())))
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        request.setActionType(ActionType.DELETE);
        // when
        service.updateTrainerHours(request);
        // then
        verify(repository).save(trainer);
        assertTrue(trainer.getMonthlyHoursByYear().get(2025).isEmpty());
    }


    @Test
    void whenGetHoursForTrainerInMonth_noDataForYear_shouldReturnZero() {
        // given
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(new HashMap<>()) // пусто
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        // when
        double hours = service.getHoursForTrainerInMonth("Nina.Petrova", 2025, 1);
        // then
        assertEquals(0.0, hours);
    }


    @Test
    void whenGetHoursForTrainerInMonth_noDataForMonth_shouldReturnZero() {
        // given
        MonthSummary summary = new MonthSummary(2, 5.0); // февраль
        TrainerMonthlyHours trainer = TrainerMonthlyHours.builder()
                .username("Nina.Petrova")
                .monthlyHoursByYear(Map.of(2025, List.of(summary)))
                .build();
        when(repository.findById("Nina.Petrova")).thenReturn(Optional.of(trainer));
        // when
        double hours = service.getHoursForTrainerInMonth("Nina.Petrova", 2025, 1); // январь
        // then
        assertEquals(0.0, hours);
    }

}

