package task.healthyhabits.resolvers.reminders;

import lombok.RequiredArgsConstructor;
import task.healthyhabits.dtos.pages.PageDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.reminder.ReminderService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class ReminderQueryResolver {

    private final ReminderService reminderService;
    private final UserRepository userRepository;

    @QueryMapping
    public ReminderPage listReminders(@Argument int page, @Argument int size) {
        requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ReminderDTO> reminderPage = PageDTO.from(reminderService.list(pageable));
        return ReminderPage.from(reminderPage);
    }

    @QueryMapping
    public ReminderPage listMyReminders(@Argument int page, @Argument int size) {
        requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR);
        Long userId = userRepository
                .findByEmail(org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ReminderDTO> reminderPage = PageDTO.from(reminderService.myReminders(userId, pageable));
        return ReminderPage.from(reminderPage);
    }

    @QueryMapping
    public ReminderDTO getReminderById(@Argument Long id) {
        requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR);
        return reminderService.findByIdOrNull(id);
    }

    public record ReminderPage(List<ReminderDTO> content, int totalPages, long totalElements, int size, int number) {
        public static ReminderPage from(PageDTO<ReminderDTO> dto) {
            return new ReminderPage(dto.content(), dto.totalPages(), dto.totalElements(), dto.size(), dto.number());
        }
    }
}
