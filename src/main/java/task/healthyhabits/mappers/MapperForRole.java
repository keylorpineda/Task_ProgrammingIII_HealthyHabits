package task.healthyhabits.mappers;

import task.healthyhabits.models.Role;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;

public class MapperForRole {

    public static Role toModel(RoleInputDTO input) {
        if (input == null) return null;

        Role role = new Role();
        role.setName(input.getName());
        role.setPermissions(input.getPermissions());
        return role;
    }

    public static RoleDTO toDTO(Role model) {
        if (model == null) return null;

        return new RoleDTO(
            model.getId(),
            model.getName(),
            model.getPermissions()
        );
    }

    public static RoleOutputDTO toOutput(Role model) {
        if (model == null) return null;

        return new RoleOutputDTO(
            model.getId(),
            model.getName(),
            model.getPermissions()
        );
    }

    public static Role fromDTO(RoleDTO dto) {
        if (dto == null) return null;

        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setPermissions(dto.getPermissions());
        return role;
    }
}
