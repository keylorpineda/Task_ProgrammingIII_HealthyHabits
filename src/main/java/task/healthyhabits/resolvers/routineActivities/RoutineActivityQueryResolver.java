package task.healthyhabits.resolvers.routineActivities;

import lombok.RequiredArgsConstructor;
import task.healthyhabits.dtos.pages.PageDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.routineActivity.RoutineActivityService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoutineActivityQueryResolver {

    private final RoutineActivityService routineActivityService;

    @QueryMapping
    public RoutineActivityPage listRoutineActivities(@Argument int page, @Argument int size) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<RoutineActivityDTO> routineActivityPage = PageDTO.from(routineActivityService.list(pageable));
        return RoutineActivityPage.from(routineActivityPage);
    }

    @QueryMapping
    public RoutineActivityDTO getRoutineActivityById(@Argument Long id) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        return routineActivityService.findByIdOrNull(id);
    }

    public record RoutineActivityPage(
            List<RoutineActivityDTO> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean hasNext,
            boolean hasPrevious) {
        public static RoutineActivityPage from(PageDTO<RoutineActivityDTO> dto) {
            return new RoutineActivityPage(
                    dto.content(),
                    dto.totalPages(),
                    dto.totalElements(),
                    dto.size(),
                    dto.number(),
                    dto.hasNext(),
                    dto.hasPrevious());
        }
    }
}
