package task.healthyhabits.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.AppUserDetailsService;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService service;

    @Test
    void loadUserByUsernameReturnsUserDetailsWithAuthorities() {
        Role role1 = new Role(1L, "ADMIN", Permission.USER_EDITOR);
        Role role2 = new Role(2L, "VIEWER", Permission.HABIT_READ);
        User user = new User(5L, "Carol", "carol@example.com", "hash", List.of(role1, role2), null, null);
        when(userRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("carol@example.com");

        assertThat(details.getUsername()).isEqualTo("carol@example.com");
        assertThat(details.getPassword()).isEqualTo("hash");
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(Permission.USER_EDITOR.name(), Permission.HABIT_READ.name());
    }

    @Test
    void loadUserByUsernameThrowsWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}