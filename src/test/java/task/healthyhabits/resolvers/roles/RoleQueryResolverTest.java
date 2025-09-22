package task.healthyhabits.resolvers.roles;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.roles.RoleQueryResolver;
import task.healthyhabits.resolvers.roles.RoleQueryResolver.RolePage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.role.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleQueryResolverTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleQueryResolver resolver;

    @Test
    void listRolesReturnsServicePage() {
        Pageable pageable = PageRequest.of(0, 5);
        RoleDTO auditor = new RoleDTO(1L, "Auditor", Permission.AUDITOR);
        Page<RoleDTO> page = new PageImpl<>(List.of(auditor), pageable, 1);
        when(roleService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RolePage result = resolver.listRoles(0, 5);

            assertThat(result.content()).containsExactly(auditor);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
        }

        verify(roleService).list(pageable);
    }

    @Test
    void getRoleByIdDelegatesToService() {
        RoleDTO role = new RoleDTO(3L, "Editor", Permission.USER_EDITOR);
        when(roleService.findByIdOrNull(3L)).thenReturn(role);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoleDTO result = resolver.getRoleById(3L);
            assertThat(result).isEqualTo(role);
        }

        verify(roleService).findByIdOrNull(3L);
    }
}
