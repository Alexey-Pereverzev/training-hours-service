package org.example.training_hours_service.controller;

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
public class TrainerMonthlyHoursController {

    private final TrainerMonthlyHoursService service;

    @PostMapping("/events")
    public ResponseEntity<String> updateTrainingHours(@RequestBody TrainingUpdateRequest request) {
        service.updateTrainerHours(request);
        return ResponseEntity.ok("Info updated successfully");
    }

    @GetMapping("/{username}/hours")
    public ResponseEntity<Double> getTrainerHours(
            @PathVariable String username,
            @RequestParam int year,
            @RequestParam int month
    ) {
        double hours = service.getHoursForTrainerInMonth(username, year, month);
        return ResponseEntity.ok(hours);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearAllTrainerHours() {
        service.clearAll();
        return ResponseEntity.noContent().build();
    }


}
