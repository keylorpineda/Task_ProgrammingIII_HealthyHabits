package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "routine_activities")
@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class RoutineActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "duration", nullable = false) 
    private Integer duration;

    @Column(name = "target_time", nullable = false)
    private Integer targetTime;

    @Column(name = "notes", length = 255)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

}
