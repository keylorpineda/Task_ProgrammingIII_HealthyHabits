package task.healthyhabits.resolvers.routineActivities;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        Page<RoutineActivityDTO> p = routineActivityService.list(pageable);
        return new RoutineActivityPage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public RoutineActivityDTO getRoutineActivityById(@Argument Long id) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        return routineActivityService.findByIdOrNull(id);
    }

    public record RoutineActivityPage(List<RoutineActivityDTO> content, int totalPages, int totalElements, int size, int number) { }
}
