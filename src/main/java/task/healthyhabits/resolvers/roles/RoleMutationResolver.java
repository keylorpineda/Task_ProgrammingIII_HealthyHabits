package task.healthyhabits.resolvers.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.role.RoleService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class RoleMutationResolver {

    private final RoleService roleService;

    @MutationMapping
    public RoleOutputDTO createRole(@Argument("input") @Valid RoleInputDTO input) {
        requireAny(Permission.USER_EDITOR);
        return roleService.create(input);
    }

    @MutationMapping
    public RoleOutputDTO updateRole(@Argument Long id, @Argument("input") @Valid RoleInputDTO input) {
        requireAny(Permission.USER_EDITOR);
        return roleService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteRole(@Argument Long id) {
        requireAny(Permission.USER_EDITOR);
        return roleService.delete(id);
    }
}
