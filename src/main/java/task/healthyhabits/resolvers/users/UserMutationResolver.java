package task.healthyhabits.resolvers.users;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.user.UserService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class UserMutationResolver {

    private final UserService userService;

    @MutationMapping
    public UserOutputDTO createUser(@Argument("input") @Valid UserInputDTO input) {
        requireAny(Permission.USER_EDITOR);
        return userService.create(input);
    }

    @MutationMapping
    public UserOutputDTO updateUser(@Argument Long id, @Argument("input") @Valid UserInputDTO input) {
        requireAny(Permission.USER_EDITOR);
        return userService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        requireAny(Permission.USER_EDITOR);
        return userService.delete(id);
    }
}
