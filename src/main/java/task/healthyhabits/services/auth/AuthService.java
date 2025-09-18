package task.healthyhabits.services.auth;

import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;

public interface AuthService {
    AuthTokenOutputDTO register(UserInputDTO input);
    AuthTokenOutputDTO login(String email, String password);
    AuthTokenOutputDTO verifyToken(String token);
}
