package task.healthyhabits.mappers;

import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;

public class MapperForCompletedActivity {

    public static CompletedActivity toModel(CompletedActivityInputDTO input) {
        if (input == null) return null;

        CompletedActivity ca = new CompletedActivity();
        ca.setHabit(MapperForHabit.toModel(input.getHabit()));
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());
        ca.setProgressLog(MapperForProgressLog.toModel(input.getProgressLog()));
        return ca;
    }

    public static CompletedActivityOutputDTO toOutput(CompletedActivity model) {
        if (model == null) return null;

        return new CompletedActivityOutputDTO(
            model.getId(),
            MapperForHabit.toOutput(model.getHabit()),
            model.getCompletedAt(),
            model.getNotes(),
            MapperForProgressLog.toOutput(model.getProgressLog())
        );
    }

    public static CompletedActivityDTO toDTO(CompletedActivity model) {
        if (model == null) return null;

        return new CompletedActivityDTO(
            model.getId(),
            MapperForHabit.toDTO(model.getHabit()),
            model.getCompletedAt(),
            model.getNotes(),
            MapperForProgressLog.toDTO(model.getProgressLog())
        );
    }

    public static CompletedActivity fromDTO(CompletedActivityDTO dto) {
        if (dto == null) return null;

        CompletedActivity ca = new CompletedActivity();
        ca.setId(dto.getId());
        ca.setHabit(MapperForHabit.fromDTO(dto.getHabit()));
        ca.setCompletedAt(dto.getCompletedAt());
        ca.setNotes(dto.getNotes());
        ca.setProgressLog(MapperForProgressLog.fromDTO(dto.getProgressLog()));
        return ca;
    }
}
