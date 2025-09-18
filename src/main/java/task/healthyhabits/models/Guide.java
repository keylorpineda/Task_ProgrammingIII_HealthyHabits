package task.healthyhabits.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "objective", nullable = false, length = 100)
    private String objective;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "guide_habit", joinColumns = @JoinColumn(name = "guide_id"), inverseJoinColumns = @JoinColumn(name = "habit_id"))
    private List<Habit> recommendedFor;
}
