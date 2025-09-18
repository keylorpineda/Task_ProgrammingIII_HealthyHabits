package task.healthyhabits.resolvers.habits;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.habit.HabitService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class HabitMutationResolver {

    private final HabitService habitService;

    @MutationMapping
    public HabitOutputDTO createHabit(@Argument("input") @Valid HabitInputDTO input) {
        requireAny(Permission.HABIT_EDITOR);
        return habitService.create(input);
    }

    @MutationMapping
    public HabitOutputDTO updateHabit(@Argument Long id, @Argument("input") @Valid HabitInputDTO input) {
        requireAny(Permission.HABIT_EDITOR);
        return habitService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteHabit(@Argument Long id) {
        requireAny(Permission.HABIT_EDITOR);
        return habitService.delete(id);
    }
}
