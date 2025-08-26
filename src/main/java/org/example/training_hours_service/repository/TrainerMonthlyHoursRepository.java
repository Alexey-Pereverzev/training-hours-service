package org.example.training_hours_service.repository;

import org.example.training_hours_service.entity.TrainerMonthlyHours;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TrainerMonthlyHoursRepository extends MongoRepository<TrainerMonthlyHours, String> {

    Optional<TrainerMonthlyHours> findByUsername(String username);
}
