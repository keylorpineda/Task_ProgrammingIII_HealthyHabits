package task.healthyhabits.models;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "guides")
@Data
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "category", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
        name = "guide_habit",
        joinColumns = @JoinColumn(name = "guide_id"),
        inverseJoinColumns = @JoinColumn(name = "habit_id")
    )
    private List<Habit> recommendedFor;
}
