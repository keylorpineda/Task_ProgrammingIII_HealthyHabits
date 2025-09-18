package task.healthyhabits.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import task.healthyhabits.models.Permission;

import java.util.Arrays;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static void requireAny(Permission... permissions) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            throw new SecurityException("Unauthorized");
        }
        boolean hasAuditor = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(name -> name.equals(Permission.AUDITOR.name())
                        || ("ROLE_" + Permission.AUDITOR.name()).equals(name));
        for (GrantedAuthority ga : auth.getAuthorities()) {
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
        throw new SecurityException("Forbidden");
    }
}
