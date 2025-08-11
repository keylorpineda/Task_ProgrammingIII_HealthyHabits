package task.healthyhabits.mappers;

import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;

public class MapperForRoutineActivity {

    public static RoutineActivity toModel(RoutineActivityInputDTO input) {
        if (input == null) return null;

        RoutineActivity activity = new RoutineActivity();
        activity.setHabit(MapperForHabit.toModel(input.getHabit()));
        activity.setDuration(input.getDuration());
        activity.setTargetTime(input.getTargetTime());
        activity.setNotes(input.getNotes());
        activity.setRoutine(MapperForRoutine.toModel(input.getRoutine()));

        return activity;
    }

    public static RoutineActivityOutputDTO toOutput(RoutineActivity model) {
        if (model == null) return null;

        return new RoutineActivityOutputDTO(
            model.getId(),
            MapperForHabit.toOutput(model.getHabit()),
            model.getDuration(),
            model.getTargetTime(),
            model.getNotes(),
            MapperForRoutine.toOutput(model.getRoutine())
        );
    }

    public static RoutineActivityDTO toDTO(RoutineActivity model) {
        if (model == null) return null;

        return new RoutineActivityDTO(
            model.getId(),
            MapperForHabit.toDTO(model.getHabit()),
            model.getDuration(),
            model.getTargetTime(),
            model.getNotes(),
            MapperForRoutine.toDTO(model.getRoutine())
        );
    }

    public static RoutineActivity fromDTO(RoutineActivityDTO dto) {
        if (dto == null) return null;

        RoutineActivity activity = new RoutineActivity();
        activity.setId(dto.getId());
        activity.setHabit(MapperForHabit.fromDTO(dto.getHabit()));
        activity.setDuration(dto.getDuration());
        activity.setTargetTime(dto.getTargetTime());
        activity.setNotes(dto.getNotes());
        activity.setRoutine(MapperForRoutine.fromDTO(dto.getRoutine()));

        return activity;
    }
}
