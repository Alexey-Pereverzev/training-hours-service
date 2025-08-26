package org.example.training_hours_service.service;

import org.example.training_hours_service.dto.TrainingUpdateRequest;


public interface TrainerMonthlyHoursService {
    void updateTrainerHours(TrainingUpdateRequest request);
    double getHoursForTrainerInMonth(String username, int year, int month);
    void clearAll();
}
