package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "habits")
@Data
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "description", nullable = false, length = 200)
    private String description;
}
