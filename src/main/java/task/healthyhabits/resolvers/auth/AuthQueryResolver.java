package task.healthyhabits.resolvers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;
import task.healthyhabits.services.auth.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthQueryResolver {

    private final AuthService authService;

    @QueryMapping
    public AuthTokenOutputDTO verifyToken(@Argument String token) {
        return authService.verifyToken(token);
    }
}