package task.healthyhabits.mappers;

import task.healthyhabits.models.AuthToken;
import task.healthyhabits.dtos.inputs.AuthTokenInputDTO;
import task.healthyhabits.dtos.normals.AuthTokenDTO;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;

public class MapperForAuthToken {

    public static AuthToken toModel(AuthTokenInputDTO input) {
        if (input == null) return null;

        AuthToken token = new AuthToken();
        token.setToken(input.getToken());
        token.setExpiresAt(input.getExpiresAt());
        token.setUser(MapperForUser.toModel(input.getUser()));

        return token;
    }

    public static AuthTokenDTO toDTO(AuthToken model) {
        if (model == null) return null;

        return new AuthTokenDTO(
            model.getToken(),
            model.getExpiresAt(),
            MapperForUser.toDTO(model.getUser())
        );
    }

    public static AuthTokenOutputDTO toOutput(AuthToken model) {
        if (model == null) return null;

        return new AuthTokenOutputDTO(
            model.getToken(),
            model.getExpiresAt(),
            MapperForUser.toOutput(model.getUser())
        );
    }

    public static AuthToken fromDTO(AuthTokenDTO dto) {
        if (dto == null) return null;

        AuthToken token = new AuthToken();
        token.setToken(dto.getToken());
        token.setExpiresAt(dto.getExpiresAt());
        token.setUser(MapperForUser.fromDTO(dto.getUser()));

        return token;
    }
}
