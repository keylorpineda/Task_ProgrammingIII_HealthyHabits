package task.healthyhabits.tests.resolvers.roles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.roles.RoleMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.role.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleMutationResolverTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleMutationResolver resolver;

    @Test
    void createRoleDelegatesToService() {
        RoleInputDTO input = new RoleInputDTO("Coach", Permission.USER_EDITOR);
        RoleOutputDTO output = new RoleOutputDTO(1L, "Coach", Permission.USER_EDITOR);
        when(roleService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoleOutputDTO result = resolver.createRole(input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(roleService).create(input);
    }

    @Test
    void updateRoleDelegatesToService() {
        RoleInputDTO input = new RoleInputDTO("Mentor", Permission.USER_READ);
        RoleOutputDTO output = new RoleOutputDTO(2L, "Mentor", Permission.USER_READ);
        when(roleService.update(2L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoleOutputDTO result = resolver.updateRole(2L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(roleService).update(2L, input);
    }

    @Test
    void deleteRoleDelegatesToService() {
        when(roleService.delete(3L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteRole(3L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.USER_EDITOR));
        }

        verify(roleService).delete(3L);
    }
}
