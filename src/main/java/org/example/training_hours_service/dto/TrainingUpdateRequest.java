package org.example.training_hours_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


@Data
@Schema(description = "Event for updating trainer's monthly hours")
public class TrainingUpdateRequest {

    @Schema(description = "Trainer username", example = "Elena.Sokolova", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainerUsername;

    @Schema(description = "Trainer first name", example = "Elena")
    private String trainerFirstName;

    @Schema(description = "Trainer last name", example = "Sokolova")
    private String trainerLastName;

    @Schema(description = "Active status of the trainer", example = "true")
    private boolean active;

    @Schema(description = "Date of the training", example = "2024-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate trainingDate;

    @Schema(description = "Training duration in minutes", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    private int trainingDuration;

    @Schema(description = "Action type: ADD or DELETE", example = "ADD", requiredMode = Schema.RequiredMode.REQUIRED)
    private ActionType actionType;
}
