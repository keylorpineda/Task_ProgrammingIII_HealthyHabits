package task.healthyhabits.mappers;

import task.healthyhabits.models.Routine;
import task.healthyhabits.models.User;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;

import java.util.Collections;
import java.util.stream.Collectors;

public class MapperForRoutine {

    public static Routine toModel(RoutineInputDTO input) {
        if (input == null)
            return null;
        Routine routine = new Routine();
        routine.setTitle(input.getTitle());
        routine.setUser(refUser(input.getUserId()));
        routine.setDescription(input.getDescription());
        routine.setDaysOfWeek(input.getDaysOfWeek());
        if (input.getActivityInputs() != null) {
            routine.setActivities(
                    input.getActivityInputs().stream()
                            .map(MapperForRoutineActivity::toModel)
                            .peek(ra -> ra.setRoutine(routine))
                            .collect(Collectors.toList()));
        }
        return routine;
    }

    public static RoutineOutputDTO toOutput(Routine model) {
        if (model == null)
            return null;
        return new RoutineOutputDTO(
                model.getId(),
                model.getTitle(),
                MapperForUser.toOutput(model.getUser()),
                model.getDescription(),
                model.getDaysOfWeek(),
                model.getActivities() == null ? Collections.emptyList()
                        : model.getActivities().stream()
                                .map(ra -> new RoutineActivityOutputDTO(
                                        ra.getId(),
                                        MapperForHabit.toOutput(ra.getHabit()),
                                        ra.getDuration(),
                                        ra.getTargetTime(),
                                        ra.getNotes(),
                                        null))
                                .collect(Collectors.toList()));
    }

    public static RoutineDTO toDTO(Routine model) {
        if (model == null)
            return null;
        return new RoutineDTO(
                model.getId(),
                model.getTitle(),
                MapperForUser.toDTO(model.getUser()),
                model.getDescription(),
                model.getDaysOfWeek(),
                model.getActivities() == null ? Collections.emptyList()
                        : model.getActivities().stream()
                                .map(ra -> new RoutineActivityDTO(
                                        ra.getId(),
                                        MapperForHabit.toDTO(ra.getHabit()),
                                        ra.getDuration(),
                                        ra.getTargetTime(),
                                        ra.getNotes(),
                                        null))
                                .collect(Collectors.toList()));
    }

    public static Routine fromDTO(RoutineDTO dto) {
        if (dto == null)
            return null;
        Routine routine = new Routine();
        routine.setId(dto.getId());
        routine.setTitle(dto.getTitle());
        routine.setUser(MapperForUser.fromDTO(dto.getUser()));
        routine.setDescription(dto.getDescription());
        routine.setDaysOfWeek(dto.getDaysOfWeek());
        if (dto.getActivities() != null) {
            routine.setActivities(
                    dto.getActivities().stream()
                            .map(MapperForRoutineActivity::fromDTO)
                            .peek(ra -> ra.setRoutine(routine))
                            .collect(Collectors.toList()));
        }
        return routine;
    }

    private static User refUser(Long id) {
        if (id == null)
            return null;
        User u = new User();
        u.setId(id);
        return u;
    }
}
