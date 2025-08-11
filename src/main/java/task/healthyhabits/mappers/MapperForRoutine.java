package task.healthyhabits.mappers;

import task.healthyhabits.models.Routine;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;

import java.util.stream.Collectors;

public class MapperForRoutine {

    public static Routine toModel(RoutineInputDTO input) {
        if (input == null) return null;

        Routine routine = new Routine();
        routine.setTitle(input.getTitle());
        routine.setUser(MapperForUser.toModel(input.getUser()));
        routine.setDescription(input.getDescription());
        routine.setDaysOfWeek(input.getDaysOfWeek());

        routine.setActivities(
            input.getActivities().stream()
                .map(MapperForRoutineActivity::toModel)
                .collect(Collectors.toList())
        );

        return routine;
    }

    public static RoutineOutputDTO toOutput(Routine model) {
        if (model == null) return null;

        return new RoutineOutputDTO(
            model.getId(),
            model.getTitle(),
            MapperForUser.toOutput(model.getUser()),
            model.getDescription(),
            model.getDaysOfWeek(),
            model.getActivities().stream()
                .map(MapperForRoutineActivity::toOutput)
                .collect(Collectors.toList())
        );
    }

    public static RoutineDTO toDTO(Routine model) {
        if (model == null) return null;

        return new RoutineDTO(
            model.getId(),
            model.getTitle(),
            MapperForUser.toDTO(model.getUser()),
            model.getDescription(),
            model.getDaysOfWeek(),
            model.getActivities().stream()
                .map(MapperForRoutineActivity::toDTO)
                .collect(Collectors.toList())
        );
    }

    public static Routine fromDTO(RoutineDTO dto) {
        if (dto == null) return null;

        Routine routine = new Routine();
        routine.setId(dto.getId());
        routine.setTitle(dto.getTitle());
        routine.setUser(MapperForUser.fromDTO(dto.getUser()));
        routine.setDescription(dto.getDescription());
        routine.setDaysOfWeek(dto.getDaysOfWeek());

        routine.setActivities(
            dto.getActivities().stream()
                .map(MapperForRoutineActivity::fromDTO)
                .collect(Collectors.toList())
        );

        return routine;
    }
}
