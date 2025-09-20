package task.healthyhabits.services.role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> list(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(Role.class, RoleDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO findByIdOrNull(Long id) {
        return roleRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(Role.class, RoleDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public RoleOutputDTO create(RoleInputDTO input) {
        Permission perm = input.getPermission();
        Optional<Role> duplicate = roleRepository.findByPermission(perm);
        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Permission already used by another role");
        }
        InputOutputMapper<RoleInputDTO, Role, RoleOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoleInputDTO.class, Role.class, RoleOutputDTO.class);
        Role role = io.convertFromInput(input);
        if (role.getName() == null || role.getName().isBlank()) {
            role.setName(perm.name());
        }
        role = roleRepository.save(role);
        return io.convertToOutput(role);
    }

    @Override
    @Transactional
    public RoleOutputDTO update(Long id, RoleInputDTO input) {
        InputOutputMapper<RoleInputDTO, Role, RoleOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoleInputDTO.class, Role.class, RoleOutputDTO.class);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));
        if (input.getPermission() != null && input.getPermission() != role.getPermission()) {
            Optional<Role> exists = roleRepository.findByPermission(input.getPermission());
            if (exists.isPresent() && !exists.get().getId().equals(id)) {
                throw new IllegalArgumentException("Permission already used by another role");
            }
            role.setPermission(input.getPermission());
        }
        if (input.getName() != null) {
            role.setName(input.getName());
        }
        role = roleRepository.save(role);
        return io.convertToOutput(role);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!roleRepository.existsById(id)) {
            return false;
        }
        roleRepository.deleteById(id);
        return true;
    }
}
