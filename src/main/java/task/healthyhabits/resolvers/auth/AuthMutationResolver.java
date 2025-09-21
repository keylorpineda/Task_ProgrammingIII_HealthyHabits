package task.healthyhabits.resolvers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.inputs.RegisterInputDTO;
import task.healthyhabits.dtos.inputs.LoginInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;
import task.healthyhabits.services.auth.AuthService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthMutationResolver {

    private final AuthService authService;
    private static final Logger logger = LogManager.getLogger(AuthMutationResolver.class);

    @MutationMapping
     public AuthTokenOutputDTO register(@Argument("input") @Valid RegisterInputDTO input) {
        UserInputDTO u = new UserInputDTO(
                input.getName(),
                input.getEmail(),
                input.getPassword(),
                List.of(),
                List.of(),
                null);
                logger.info("Register mutation invoked");
        return authService.register(u);
    }

    @MutationMapping
    public AuthTokenOutputDTO login(@Argument("input") @Valid LoginInputDTO input) {
        logger.info("Login mutation invoked");
        return authService.login(input.getEmail(), input.getPassword());
    }
}
