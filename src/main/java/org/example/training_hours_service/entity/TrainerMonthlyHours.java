package org.example.training_hours_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@Document(collection = "trainer_monthly_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerMonthlyHours {

    @Id
    private String username;        // username as UID

    private String firstName;
    private String lastName;
    private boolean active;
    private Map<Integer, List<MonthSummary>> monthlyHoursByYear;
}
