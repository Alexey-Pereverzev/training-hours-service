package org.example.training_hours_service.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class TrainingUpdateRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;
    private LocalDate trainingDate;
    private int trainingDuration;   // in minutes
    private ActionType actionType;
}
