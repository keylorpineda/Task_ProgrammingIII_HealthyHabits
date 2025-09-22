package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import task.healthyhabits.models.Frequency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderOutputDTO {
    private Long id;
    private UserOutputDTO user;
    private HabitOutputDTO habit;
    private LocalTime time;
    private Frequency frequency;
@Override
    public String toString() {
        return "ReminderOutputDTO{" +
                "id=" + id +
                ", user=" + user +
                ", habit=" + habit +
                ", time=" + formatTime(time) +
                ", frequency=" + frequency +
                '}';
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "null";
        }
        if (LocalTime.MIDNIGHT.equals(time)) {
            return "MIDNIGHT";
        }
        if (LocalTime.NOON.equals(time)) {
            return "NOON";
        }
        if (LocalTime.MIN.equals(time)) {
            return "MIN";
        }
        if (LocalTime.MAX.equals(time)) {
            return "MAX";
        }
        return time.toString();
    }
}