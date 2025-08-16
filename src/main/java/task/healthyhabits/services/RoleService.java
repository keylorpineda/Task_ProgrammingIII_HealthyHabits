package task.healthyhabits.services;

import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.mappers.MapperForRole;

import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Page<RoleDTO> list(Pageable pageable) {
        return roleRepository.findAll(pageable).map(MapperForRole::toDTO);
    }

    @Transactional(readOnly = true)
    public RoleDTO findByIdOrNull(Long id) {
        return roleRepository.findById(id).map(MapperForRole::toDTO).orElse(null);
    }

    public RoleOutputDTO create(RoleInputDTO input) {
        Role r = MapperForRole.toModel(input);
        return MapperForRole.toOutput(roleRepository.save(r));
    }

    public RoleOutputDTO update(Long id, RoleInputDTO input) {
        Role r = roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));
        r.setName(input.getName());
        r.setPermissions(input.getPermissions());
        return MapperForRole.toOutput(roleRepository.save(r));
    }

    public boolean delete(Long id) {
        if (!roleRepository.existsById(id)) return false;
        roleRepository.deleteById(id);
        return true;
    }
}
