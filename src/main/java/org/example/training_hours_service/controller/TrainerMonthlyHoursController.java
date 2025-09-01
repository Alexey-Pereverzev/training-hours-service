package org.example.training_hours_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.example.training_hours_service.service.TrainerMonthlyHoursService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/trainer-hours")
@RequiredArgsConstructor
@Tag(name = "Trainer Hours", description = "Operations for managing trainers' monthly hours")
public class TrainerMonthlyHoursController {

    private final TrainerMonthlyHoursService service;


    @PostMapping("/events")
    @Operation(
            summary = "Update trainer hours",
            description = "Adds or subtracts training hours for a trainer based on the provided action (ADD or DELETE)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer hours updated successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "422", description = "Internal error")
    })
    public ResponseEntity<String> updateTrainingHours(
            @Parameter(description = "Training event details", required = true)
            @RequestBody TrainingUpdateRequest request) {
        service.updateTrainerHours(request);
        return ResponseEntity.ok("Info updated successfully");
    }


    @GetMapping("/{username}/hours")
    @Operation(
            summary = "Get trainer's monthly hours",
            description = "Returns the total number of hours a trainer has conducted in a given month"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total trainer hours",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<Double> getTrainerHours(
            @Parameter(description = "Trainer username", example = "Elena.Sokolova", required = true)
            @PathVariable String username,
            @Parameter(description = "Year", example = "2024", required = true)
            @RequestParam int year,
            @Parameter(description = "Month (1-12)", example = "5", required = true)
            @RequestParam int month
    ) {
        double hours = service.getHoursForTrainerInMonth(username, year, month);
        return ResponseEntity.ok(hours);
    }


    @DeleteMapping
    @Operation(
            summary = "Clear all trainer hours",
            description = "Deletes all records from trainer-hours microservice"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All trainer hours cleared"),
            @ApiResponse(responseCode = "422", description = "Internal error")
    })
    public ResponseEntity<Void> clearAllTrainerHours() {
        service.clearAll();
        return ResponseEntity.noContent().build();
    }

}
