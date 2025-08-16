package task.healthyhabits.mappers;

import task.healthyhabits.models.User;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;

import java.util.stream.Collectors;

public class MapperForUser {

    public static User toModel(UserInputDTO input) {
        if (input == null) return null;

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(input.getPassword());

        user.setRoles(
            input.getRoles().stream()
                .map(MapperForRole::toModel)
                .collect(Collectors.toList())
        );

        user.setFavoriteHabits(
            input.getFavoriteHabits().stream()
                .map(MapperForHabit::toModel)
                .collect(Collectors.toList())
        );

        return user;
    }

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRoles().stream()
                .map(MapperForRole::toDTO)
                .collect(Collectors.toList()),
            user.getFavoriteHabits().stream()
                .map(MapperForHabit::toDTO)
                .collect(Collectors.toList())
        );
    }

    public static UserOutputDTO toOutput(User user) {
        if (user == null) return null;

        return new UserOutputDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles().stream()
                .map(MapperForRole::toOutput)
                .collect(Collectors.toList()),
            user.getFavoriteHabits().stream()
                .map(MapperForHabit::toOutput)
                .collect(Collectors.toList())
        );
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        user.setRoles(
            dto.getRoles().stream()
                .map(MapperForRole::fromDTO)
                .collect(Collectors.toList())
        );

        user.setFavoriteHabits(
            dto.getFavoriteHabits().stream()
                .map(MapperForHabit::fromDTO)
                .collect(Collectors.toList())
        );

        return user;
    }
}
