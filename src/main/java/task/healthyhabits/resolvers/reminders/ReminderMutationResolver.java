package task.healthyhabits.resolvers.reminders;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.reminder.ReminderService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class ReminderMutationResolver {

    private final ReminderService reminderService;

    @MutationMapping
    public ReminderOutputDTO createReminder(@Argument("input") @Valid ReminderInputDTO input) {
        requireAny(Permission.REMINDER_EDITOR);
        return reminderService.create(input);
    }

    @MutationMapping
    public ReminderOutputDTO updateReminder(@Argument Long id, @Argument("input") @Valid ReminderInputDTO input) {
        requireAny(Permission.REMINDER_EDITOR);
        return reminderService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteReminder(@Argument Long id) {
        requireAny(Permission.REMINDER_EDITOR);
        return reminderService.delete(id);
    }
}
