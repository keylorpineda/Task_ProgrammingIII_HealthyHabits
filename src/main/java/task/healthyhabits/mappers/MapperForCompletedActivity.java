package task.healthyhabits.mappers;

import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.Habit;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;

public class MapperForCompletedActivity {

    public static CompletedActivity toModel(CompletedActivityInputDTO input) {
        if (input == null)
            return null;
        CompletedActivity ca = new CompletedActivity();
        ca.setHabit(refHabit(input.getHabitId()));
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());
        return ca;
    }

    public static CompletedActivityOutputDTO toOutput(CompletedActivity model) {
        if (model == null)
            return null;
        return new CompletedActivityOutputDTO(
                model.getId(),
                MapperForHabit.toOutput(model.getHabit()),
                model.getCompletedAt(),
                model.getNotes(),
                null);
    }

    public static CompletedActivityDTO toDTO(CompletedActivity model) {
        if (model == null)
            return null;
        return new CompletedActivityDTO(
                model.getId(),
                MapperForHabit.toDTO(model.getHabit()),
                model.getCompletedAt(),
                model.getNotes(),
                null);
    }

    public static CompletedActivity fromDTO(CompletedActivityDTO dto) {
        if (dto == null)
            return null;
        CompletedActivity ca = new CompletedActivity();
        ca.setId(dto.getId());
        ca.setHabit(MapperForHabit.fromDTO(dto.getHabit()));
        ca.setCompletedAt(dto.getCompletedAt());
        ca.setNotes(dto.getNotes());
        if (dto.getProgressLog() != null) {
            ca.setProgressLog(MapperForProgressLog.fromDTO(dto.getProgressLog()));
        }
        return ca;
    }

    private static Habit refHabit(Long id) {
        if (id == null)
            return null;
        Habit h = new Habit();
        h.setId(id);
        return h;
    }
}
