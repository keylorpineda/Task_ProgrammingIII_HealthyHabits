package task.healthyhabits.mappers;

import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;

import java.util.stream.Collectors;

public class MapperForProgressLog {

    public static ProgressLog toModel(ProgressLogInputDTO input) {
        if (input == null) return null;

        ProgressLog log = new ProgressLog();
        log.setUser(MapperForUser.toModel(input.getUser()));
        log.setRoutine(MapperForRoutine.toModel(input.getRoutine()));
        log.setDate(input.getDate());

        // Nota: completedActivities no viene en el input
        return log;
    }

    public static ProgressLogOutputDTO toOutput(ProgressLog model) {
        if (model == null) return null;

        return new ProgressLogOutputDTO(
            model.getId(),
            MapperForUser.toOutput(model.getUser()),
            MapperForRoutine.toOutput(model.getRoutine()),
            model.getDate(),
            model.getCompletedActivities().stream()
                .map(MapperForCompletedActivity::toOutput)
                .collect(Collectors.toList())
        );
    }

    public static ProgressLogDTO toDTO(ProgressLog model) {
        if (model == null) return null;

        return new ProgressLogDTO(
            model.getId(),
            MapperForUser.toDTO(model.getUser()),
            MapperForRoutine.toDTO(model.getRoutine()),
            model.getDate(),
            model.getCompletedActivities().stream()
                .map(MapperForCompletedActivity::toDTO)
                .collect(Collectors.toList())
        );
    }

    public static ProgressLog fromDTO(ProgressLogDTO dto) {
        if (dto == null) return null;

        ProgressLog log = new ProgressLog();
        log.setId(dto.getId());
        log.setUser(MapperForUser.fromDTO(dto.getUser()));
        log.setRoutine(MapperForRoutine.fromDTO(dto.getRoutine()));
        log.setDate(dto.getDate());
        log.setCompletedActivities(
            dto.getCompletedActivities().stream()
                .map(MapperForCompletedActivity::fromDTO)
                .collect(Collectors.toList())
        );
        return log;
    }
}
