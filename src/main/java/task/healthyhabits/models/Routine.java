package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "routines")
@Data
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @ElementCollection(targetClass = DaysOfWeek.class)
    @CollectionTable(name = "routine_days_of_week", joinColumns = @JoinColumn(name = "routine_id"))
    @Column(name = "day_of_week", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private List<DaysOfWeek> daysOfWeek;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineActivity> activities;
}
