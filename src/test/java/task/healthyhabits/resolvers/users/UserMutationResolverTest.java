package task.healthyhabits.resolvers.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.users.UserMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class UserMutationResolverTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserMutationResolver resolver;

    @Test
    void createUserDelegatesToServiceAndChecksPermission() {
        UserInputDTO input = new UserInputDTO(
                "Dana",
                "dana@example.com",
                "password123",
                List.of(new RoleInputDTO("Coach", Permission.USER_EDITOR)),
                List.of(new HabitInputDTO("Hydrate", Category.DIET, "Drink water")),
                5L);
        UserOutputDTO output = new UserOutputDTO(
                9L,
                "Dana",
                "dana@example.com",
                List.of(new RoleOutputDTO(1L, "Coach", Permission.USER_EDITOR)),
                List.of(new HabitOutputDTO(4L, "Hydrate", Category.DIET, "Drink water")),
                5L);
        when(userService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UserOutputDTO result = resolver.createUser(input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(userService).create(input);
    }

    @Test
    void updateUserDelegatesToServiceAndChecksPermission() {
        UserInputDTO input = new UserInputDTO(
                "Dana",
                "dana@example.com",
                "password123",
                List.of(new RoleInputDTO("Coach", Permission.USER_EDITOR)),
                List.of(new HabitInputDTO("Hydrate", Category.DIET, "Drink water")),
                5L);
        UserOutputDTO output = new UserOutputDTO(
                9L,
                "Dana",
                "dana@example.com",
                List.of(new RoleOutputDTO(1L, "Coach", Permission.USER_EDITOR)),
                List.of(new HabitOutputDTO(4L, "Hydrate", Category.DIET, "Drink water")),
                5L);
        when(userService.update(9L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UserOutputDTO result = resolver.updateUser(9L, input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(userService).update(9L, input);
    }

    @Test
    void deleteUserDelegatesToServiceAndChecksPermission() {
        when(userService.delete(7L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteUser(7L);

            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(userService).delete(7L);
    }
}