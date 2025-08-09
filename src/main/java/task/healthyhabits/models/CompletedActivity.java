package task.healthyhabits.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "completed_activities")
@Data
public class CompletedActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_activity_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @Column(name = "completed_at", nullable = false, length = 100)
    private LocalDateTime completedAt;

    @Column(name = "notes", length = 200)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "progress_log_id", nullable = false)
    private ProgressLog progressLog;

}
