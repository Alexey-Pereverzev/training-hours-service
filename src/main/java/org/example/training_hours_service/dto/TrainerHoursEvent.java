package org.example.training_hours_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer hours event")
public class TrainerHoursEvent {
    @Schema(description = "Transaction ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String txId;

    @Schema(description = "Event type", example = "UPDATE")
    private EventType type;

    @Schema(description = "Training update payload (only for UPDATE (ADD/DELETE) events)")
    private TrainingUpdateRequest trainingUpdate;
}

