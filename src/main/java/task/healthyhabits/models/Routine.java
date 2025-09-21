package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @ElementCollection
    @CollectionTable(
        name = "routine_tags",
        joinColumns = @JoinColumn(name = "routine_id")
    )
    @Column(name = "tag", nullable = false, length = 50)
    private List<String> tags = new ArrayList<>();

    @ElementCollection(targetClass = DaysOfWeek.class)
    @CollectionTable(
        name = "routine_days_of_week",
        joinColumns = @JoinColumn(name = "routine_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"routine_id", "day_of_week"})
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private List<DaysOfWeek> daysOfWeek;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineActivity> activities;
}
