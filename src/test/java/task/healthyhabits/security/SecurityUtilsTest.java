package task.healthyhabits.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import task.healthyhabits.models.Permission;
import task.healthyhabits.security.SecurityUtils;

class SecurityUtilsTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireAnyAllowsMatchingAuthority() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "user", "pw", Permission.HABIT_READ.name());
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatCode(() -> SecurityUtils.requireAny(Permission.HABIT_READ)).doesNotThrowAnyException();
    }

    @Test
    void requireAnyAllowsAuditorForReadPermissions() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "auditor", "pw", Collections.singleton(new SimpleGrantedAuthority(Permission.AUDITOR.name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatCode(() -> SecurityUtils.requireAny(Permission.USER_READ)).doesNotThrowAnyException();
    }

    @Test
    void requireAnyRejectsAuditorForWritePermissions() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "auditor", "pw", Collections.singleton(new SimpleGrantedAuthority(Permission.AUDITOR.name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> SecurityUtils.requireAny(Permission.USER_EDITOR))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access is denied.");
    }

    @Test
    void requireAnyThrowsWhenNoAuthentication() {
        assertThatThrownBy(() -> SecurityUtils.requireAny(Permission.USER_READ))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Authentication is required to access this resource.");
    }

    @Test
    void requireAnyThrowsWhenNoAuthorities() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("user", "pw");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> SecurityUtils.requireAny(Permission.USER_READ))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access is denied.");
    }

    @Test
    void requireAnyThrowsWhenNoMatchingPermissions() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user", "pw", Collections.singleton(new SimpleGrantedAuthority(Permission.USER_READ.name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> SecurityUtils.requireAny(Permission.HABIT_EDITOR))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access is denied.");
    }
}