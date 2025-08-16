package task.healthyhabits.mappers;

import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.models.User;
import task.healthyhabits.models.Routine;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;

import java.util.Collections;
import java.util.stream.Collectors;

public class MapperForProgressLog {

    public static ProgressLog toModel(ProgressLogInputDTO input) {
        if (input == null)
            return null;
        ProgressLog log = new ProgressLog();
        log.setUser(refUser(input.getUserId()));
        log.setRoutine(refRoutine(input.getRoutineId()));
        log.setDate(input.getDate());
        if (input.getCompletedActivityInputs() != null) {
            log.setCompletedActivities(
                    input.getCompletedActivityInputs().stream()
                            .map(MapperForCompletedActivity::toModel)
                            .peek(ca -> ca.setProgressLog(log))
                            .collect(Collectors.toList()));
        }
        return log;
    }

    public static ProgressLogOutputDTO toOutput(ProgressLog model) {
        if (model == null)
            return null;
        return new ProgressLogOutputDTO(
                model.getId(),
                MapperForUser.toOutput(model.getUser()),
                MapperForRoutine.toOutput(model.getRoutine()),
                model.getDate(),
                model.getCompletedActivities() == null ? Collections.emptyList()
                        : model.getCompletedActivities().stream()
                                .map(ca -> new CompletedActivityOutputDTO(
                                        ca.getId(),
                                        MapperForHabit.toOutput(ca.getHabit()),
                                        ca.getCompletedAt(),
                                        ca.getNotes(),
                                        null))
                                .collect(Collectors.toList()));
    }

    public static ProgressLogDTO toDTO(ProgressLog model) {
        if (model == null)
            return null;
        return new ProgressLogDTO(
                model.getId(),
                MapperForUser.toDTO(model.getUser()),
                MapperForRoutine.toDTO(model.getRoutine()),
                model.getDate(),
                model.getCompletedActivities() == null ? Collections.emptyList()
                        : model.getCompletedActivities().stream()
                                .map(ca -> new CompletedActivityDTO(
                                        ca.getId(),
                                        MapperForHabit.toDTO(ca.getHabit()),
                                        ca.getCompletedAt(),
                                        ca.getNotes(),
                                        null))
                                .collect(Collectors.toList()));
    }

    public static ProgressLog fromDTO(ProgressLogDTO dto) {
        if (dto == null)
            return null;
        ProgressLog log = new ProgressLog();
        log.setId(dto.getId());
        log.setUser(MapperForUser.fromDTO(dto.getUser()));
        log.setRoutine(MapperForRoutine.fromDTO(dto.getRoutine()));
        log.setDate(dto.getDate());
        if (dto.getCompletedActivities() != null) {
            log.setCompletedActivities(
                    dto.getCompletedActivities().stream()
                            .map(MapperForCompletedActivity::fromDTO)
                            .peek(ca -> ca.setProgressLog(log))
                            .collect(Collectors.toList()));
        }
        return log;
    }

    private static User refUser(Long id) {
        if (id == null)
            return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    private static Routine refRoutine(Long id) {
        if (id == null)
            return null;
        Routine r = new Routine();
        r.setId(id);
        return r;
    }
}
