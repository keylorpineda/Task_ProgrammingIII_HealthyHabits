package task.healthyhabits.resolvers.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.user.UserService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class UserQueryResolver {

    private final UserService userService;
    private final UserRepository userRepository;

    @QueryMapping
    public UserDTO getCurrentUser() {
        return userService.getAuthenticatedUser();
    }

    @QueryMapping
    public UserPage listUsers(@Argument int page, @Argument int size) {
        requireAny(Permission.USER_READ, Permission.USER_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> p = userService.list(pageable);
        return new UserPage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public HabitPage listMyFavoriteHabits(@Argument int page, @Argument int size) {
        requireAny(Permission.HABIT_READ, Permission.HABIT_EDITOR);
        Long userId = userRepository
                .findByEmail(org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<HabitDTO> p = userService.listMyFavoriteHabits(userId, pageable);
        return new HabitPage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public UserPage listMyStudents(@Argument int page, @Argument int size) {
        requireAny(Permission.USER_READ, Permission.USER_EDITOR);
        Long coachId = userRepository
                .findByEmail(org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> p = userService.listStudentsOfCoach(coachId, pageable);
        return new UserPage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    public record UserPage(List<UserDTO> content, int totalPages, int totalElements, int size, int number) {
    }

    public record HabitPage(List<HabitDTO> content, int totalPages, int totalElements, int size, int number) {
    }
}
