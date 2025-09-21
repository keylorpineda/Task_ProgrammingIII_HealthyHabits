package task.healthyhabits.resolvers.routines;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.routine.RoutineService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoutineQueryResolver {

    private final RoutineService routineService;
    private final UserRepository userRepository;

    @QueryMapping
    public RoutinePage listRoutines(@Argument int page, @Argument int size) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineDTO> p = routineService.list(pageable);
        return new RoutinePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public RoutinePage listMyRoutines(@Argument int page, @Argument int size) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        Long userId = userRepository
                .findByEmail(org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineDTO> p = routineService.myRoutines(userId, pageable);
        return new RoutinePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public RoutinePage listRoutinesByUser(@Argument Long userId, @Argument int page, @Argument int size) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<RoutineDTO> p = routineService.byUser(userId, pageable);
        return new RoutinePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public RoutineDTO getRoutineById(@Argument Long id) {
        requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR);
        return routineService.findByIdOrNull(id);
    }

    public record RoutinePage(List<RoutineDTO> content, int totalPages, int totalElements, int size, int number) {
    }
}
