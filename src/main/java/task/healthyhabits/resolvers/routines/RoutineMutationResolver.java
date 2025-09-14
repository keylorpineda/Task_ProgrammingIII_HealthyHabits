package task.healthyhabits.resolvers.routines;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import jakarta.validation.Valid;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.routine.RoutineService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoutineMutationResolver {

    private final RoutineService routineService;

    @MutationMapping
    public RoutineOutputDTO createRoutine(@Argument("input") @Valid RoutineInputDTO input) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineService.create(input);
    }

    @MutationMapping
    public RoutineOutputDTO updateRoutine(@Argument Long id, @Argument("input") @Valid RoutineInputDTO input) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineService.delete(id);
    }
}
