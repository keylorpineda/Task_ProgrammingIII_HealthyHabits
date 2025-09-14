package task.healthyhabits.resolvers.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.role.RoleService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoleQueryResolver {

    private final RoleService roleService;

    @QueryMapping
    public RolePage listRoles(@Argument int page, @Argument int size) {
        requireAny(Permission.USER_READ, Permission.USER_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleDTO> p = roleService.list(pageable);
        return new RolePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public RoleDTO getRoleById(@Argument Long id) {
        requireAny(Permission.USER_READ, Permission.USER_EDITOR);
        return roleService.findByIdOrNull(id);
    }

    public record RolePage(List<RoleDTO> content, int totalPages, int totalElements, int size, int number) { }
}
