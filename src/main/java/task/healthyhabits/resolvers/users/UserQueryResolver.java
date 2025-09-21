package task.healthyhabits.resolvers.users;

import lombok.RequiredArgsConstructor;
import task.healthyhabits.dtos.pages.PageDTO;
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
        PageDTO<UserDTO> userPageDTO = PageDTO.from(userService.list(pageable));
        return UserPage.from(userPageDTO);
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
        PageDTO<HabitDTO> habitPageDTO = PageDTO.from(userService.listMyFavoriteHabits(userId, pageable));
        return HabitPage.from(habitPageDTO);
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
        PageDTO<UserDTO> userPageDTO = PageDTO.from(userService.listStudentsOfCoach(coachId, pageable));
        return UserPage.from(userPageDTO);
    }

    public record UserPage(
            List<UserDTO> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean hasNext,
            boolean hasPrevious) {
        public static UserPage from(PageDTO<UserDTO> dto) {
            return new UserPage(
                    dto.content(),
                    dto.totalPages(),
                    dto.totalElements(),
                    dto.size(),
                    dto.number(),
                    dto.hasNext(),
                    dto.hasPrevious());
        }
    }

    public record HabitPage(
            List<HabitDTO> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean hasNext,
            boolean hasPrevious) {
        public static HabitPage from(PageDTO<HabitDTO> dto) {
            return new HabitPage(
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
