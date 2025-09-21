package task.healthyhabits.security;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import task.healthyhabits.models.Permission;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static void requireAny(Permission... permissions) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required to access this resource.");
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            throw new AccessDeniedException("Access is denied.");
        }
        boolean hasAuditor = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(name -> name.equals(Permission.AUDITOR.name())
                        || ("ROLE_" + Permission.AUDITOR.name()).equals(name));
        for (GrantedAuthority ga : authorities) {
            String name = ga.getAuthority();
            boolean match = Arrays.stream(permissions)
                    .anyMatch(p -> p.name().equals(name) || ("ROLE_" + p.name()).equals(name));
            if (match) {
                return;
            }
        }
        if (hasAuditor && Arrays.stream(permissions).anyMatch(p -> p.name().endsWith("_READ"))) {
            return;
        }
        throw new AccessDeniedException("Access is denied.");
    }
}
