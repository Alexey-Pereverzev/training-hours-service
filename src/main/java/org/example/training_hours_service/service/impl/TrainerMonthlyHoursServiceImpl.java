package org.example.training_hours_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.training_hours_service.dto.ActionType;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.example.training_hours_service.entity.MonthSummary;
import org.example.training_hours_service.entity.TrainerMonthlyHours;
import org.example.training_hours_service.repository.TrainerMonthlyHoursRepository;
import org.example.training_hours_service.service.TrainerMonthlyHoursService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TrainerMonthlyHoursServiceImpl implements TrainerMonthlyHoursService {

    private final TrainerMonthlyHoursRepository repository;

    private static final Logger log = LoggerFactory.getLogger(TrainerMonthlyHoursServiceImpl.class);


    @Override
    public void updateTrainerHours(TrainingUpdateRequest request) {
        log.info("Updating training hours for trainer: {}, action: {}", request.getTrainerUsername(), request.getActionType());

        String username = request.getTrainerUsername();
        LocalDate date = request.getTrainingDate();
        int year = date.getYear();
        int month = date.getMonthValue();
        double durationHours = request.getTrainingDuration() / 60.0;    //  minutes -> hours

        log.info("Processing request for {} trainer hours {} for {}-{} (action: {})",
                durationHours, username, year, month, request.getActionType());

        TrainerMonthlyHours trainer = repository.findById(username)
                .orElseGet(() -> {
                    log.info("Create a new entry for a trainer {}", username);
                    TrainerMonthlyHours newTrainer = new TrainerMonthlyHours();
                    newTrainer.setUsername(username);
                    newTrainer.setMonthlyHoursByYear(new HashMap<>());
                    return newTrainer;
                });

        trainer.setFirstName(request.getTrainerFirstName());
        trainer.setLastName(request.getTrainerLastName());
        trainer.setActive(request.isActive());

        Map<Integer, List<MonthSummary>> map = trainer.getMonthlyHoursByYear();
        map.putIfAbsent(year, new ArrayList<>());

        List<MonthSummary> monthSummaries = map.get(year);
        Optional<MonthSummary> monthOpt = monthSummaries.stream()
                .filter(m -> m.getMonth() == month)
                .findFirst();

        if (monthOpt.isPresent()) {
            MonthSummary summary = monthOpt.get();
            if (request.getActionType() == ActionType.ADD) {
                summary.setTotalHours(summary.getTotalHours() + durationHours);
            } else if (request.getActionType() == ActionType.DELETE) {
                double oldHours = summary.getTotalHours();
                double newHours = oldHours - durationHours;
                if (newHours < 0) {
                    log.warn("Attempt to delete more hours ({}) than available ({}) for trainer {} in {}/{}. " +
                                    "Reset to 0.0. Possible desynchronization of microservices.",
                            durationHours, oldHours, username, month, year);
                }
                summary.setTotalHours(Math.max(0.0, newHours));
            }
        } else {
            if (request.getActionType() == ActionType.ADD) {
                monthSummaries.add(new MonthSummary(month, durationHours));
            } else {
                // when deleting, not creating a new month if it did not exist
                log.warn("Attempting to delete hours for month {} that does not exist for trainer {}", month, username);
            }
        }

        repository.save(trainer);
        log.info("Hours for trainer {} have been updated successfully", username);
    }


    @Override
    public double getHoursForTrainerInMonth(String username, int year, int month) {
        log.info("Fetching hours for trainer: {} for {}/{}", username, month, year);
        Optional<TrainerMonthlyHours> trainerOpt = repository.findById(username);
        if (trainerOpt.isEmpty()) {
            log.info("No entry found for trainer {}, returning 0.0", username);
            return 0.0;
        }
        TrainerMonthlyHours trainer = trainerOpt.get();
        Map<Integer, List<MonthSummary>> map = trainer.getMonthlyHoursByYear();
        List<MonthSummary> summaries = map.get(year);

        if (summaries == null) {
            log.info("No data for {} year for trainer {}", year, username);
            return 0.0;
        }

        return summaries.stream()
                .filter(summary -> summary.getMonth() == month)
                .findFirst()
                .map(MonthSummary::getTotalHours)
                .orElse(0.0);
    }

    @Override
    public void clearAll() {
        repository.deleteAll();
    }
}
