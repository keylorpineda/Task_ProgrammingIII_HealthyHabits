package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "progress_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_log_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "progressLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompletedActivity> completedActivities;
}
