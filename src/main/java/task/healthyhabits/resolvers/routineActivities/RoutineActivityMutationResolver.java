package task.healthyhabits.resolvers.routineActivities;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import jakarta.validation.Valid;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.routineActivity.RoutineActivityService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoutineActivityMutationResolver {

    private final RoutineActivityService routineActivityService;

    @MutationMapping
    public RoutineActivityOutputDTO createRoutineActivity(@Argument Long routineId,
                                                          @Argument("input") @Valid RoutineActivityInputDTO input) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineActivityService.create(routineId, input);
    }

    @MutationMapping
    public RoutineActivityOutputDTO updateRoutineActivity(@Argument Long id,
                                                          @Argument("input") @Valid RoutineActivityInputDTO input) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineActivityService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteRoutineActivity(@Argument Long id) {
        requireAny(Permission.ROUTINE_EDITOR);
        return routineActivityService.delete(id);
    }
}
