package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "routine_activities")
@Data
public class RoutineActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "duration", nullable = false, length = 50)
    private String duration;

    @Column(name = "target_time", nullable = false, length = 50)
    private String targetTime;

    @Column(name = "notes", length = 255)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

}
