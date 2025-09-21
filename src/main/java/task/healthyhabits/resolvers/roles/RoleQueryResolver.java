package task.healthyhabits.resolvers.roles;

import lombok.RequiredArgsConstructor;
import task.healthyhabits.dtos.pages.PageDTO;
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
        PageDTO<RoleDTO> rolePage = PageDTO.from(roleService.list(pageable));
        return RolePage.from(rolePage);
    }

    @QueryMapping
    public RoleDTO getRoleById(@Argument Long id) {
        requireAny(Permission.USER_READ, Permission.USER_EDITOR);
        return roleService.findByIdOrNull(id);
    }

    public record RolePage(List<RoleDTO> content, int totalPages, long totalElements, int size, int number) {
        public static RolePage from(PageDTO<RoleDTO> dto) {
            return new RolePage(dto.content(), dto.totalPages(), dto.totalElements(), dto.size(), dto.number());
        }
    }
 }
