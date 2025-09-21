package task.healthyhabits.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.security.AppUserDetails;

class AppUserDetailsTest {

    @Test
    void getAuthoritiesTransformsRolesWithPrefix() {
        Role admin = new Role(1L, "ADMIN", Permission.USER_EDITOR);
        Role viewer = new Role(2L, "VIEWER", Permission.USER_READ);
        User user = new User(10L, "Alice", "alice@example.com", "secret", List.of(admin, viewer), null, null);

        AppUserDetails details = new AppUserDetails(user);

        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_VIEWER");
        assertThat(details.getUsername()).isEqualTo("alice@example.com");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getDomainUser()).isSameAs(user);
    }

    @Test
    void getAuthoritiesReturnsEmptyListWhenNoRoles() {
        User user = new User(11L, "Bob", "bob@example.com", "pwd", null, null, null);

        AppUserDetails details = new AppUserDetails(user);

        assertThat(details.getAuthorities()).isEmpty();
    }
}