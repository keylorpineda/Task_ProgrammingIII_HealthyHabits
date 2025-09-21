package task.healthyhabits.resolvers.habits;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.pages.PageDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.habit.HabitService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class HabitQueryResolver {

    private final HabitService habitService;

    @QueryMapping
    public HabitPage listHabits(@Argument int page, @Argument int size) {
        requireAny(Permission.HABIT_READ, Permission.HABIT_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<HabitDTO> habitPage = PageDTO.from(habitService.list(pageable));
        return HabitPage.from(habitPage);
    }

    @QueryMapping
    public HabitPage listHabitsByCategory(@Argument Category category, @Argument int page, @Argument int size) {
        requireAny(Permission.HABIT_READ, Permission.HABIT_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<HabitDTO> habitPage = PageDTO.from(habitService.byCategory(category, pageable));
        return HabitPage.from(habitPage);
    }

    @QueryMapping
    public HabitDTO getHabitById(@Argument Long id) {
        requireAny(Permission.HABIT_READ, Permission.HABIT_EDITOR);
        return habitService.findByIdOrNull(id);
    }

    public record HabitPage(List<HabitDTO> content, int totalPages, int totalElements, int size, int number) {
        public static HabitPage from(PageDTO<HabitDTO> dto) {
            return new HabitPage(dto.content(), dto.totalPages(), dto.totalElements(), dto.size(), dto.number());
        }
    }
}
