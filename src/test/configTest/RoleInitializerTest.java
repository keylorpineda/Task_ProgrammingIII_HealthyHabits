package task.healthyhabits.configTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.config.RoleInitializer;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializer initializer;

    @Test
    void runCreatesMissingPermissionsAndAvoidsDuplicates() {
        Permission[] permissions = Permission.values();
        Set<Permission> existingPermissions = EnumSet.noneOf(Permission.class);
        if (permissions.length > 0) {
            existingPermissions.add(permissions[0]);
        }

        for (Permission permission : permissions) {
            if (existingPermissions.contains(permission)) {
                when(roleRepository.findByPermission(permission))
                        .thenReturn(Optional.of(roleWith(permission)));
            } else {
                when(roleRepository.findByPermission(permission)).thenReturn(Optional.empty());
            }
        }

        ArgumentCaptor<Role> savedRoles = ArgumentCaptor.forClass(Role.class);
        when(roleRepository.save(savedRoles.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        initializer.run();

        Set<Permission> missingPermissions = EnumSet.allOf(Permission.class);
        missingPermissions.removeAll(existingPermissions);

        for (Permission permission : permissions) {
            verify(roleRepository).findByPermission(permission);
        }

        verify(roleRepository, times(missingPermissions.size())).save(any(Role.class));

        List<Role> createdRoles = savedRoles.getAllValues();
        assertThat(createdRoles).hasSize(missingPermissions.size());
        assertThat(createdRoles)
                .extracting(Role::getPermission)
                .containsExactlyInAnyOrderElementsOf(missingPermissions);
        assertThat(createdRoles)
                .extracting(Role::getName)
                .containsExactlyInAnyOrderElementsOf(
                        missingPermissions.stream().map(Enum::name).toList());
    }

    private Role roleWith(Permission permission) {
        Role role = new Role();
        role.setPermission(permission);
        role.setName(permission.name());
        return role;
    }
}