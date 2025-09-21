package task.healthyhabits.services.role;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(RoleServiceImplementation.class);
    private final RoleRepository roleRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> list(Pageable pageable) {
        logger.info("Listing roles with pageable {}", pageable);
        try {
            Page<RoleDTO> roles = roleRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(Role.class, RoleDTO.class).convertToDTO(entity));
            logger.info("Listed {} roles", roles.getNumberOfElements());
            return roles;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing roles with pageable {}", pageable, ex);
            throw ex;
        }
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
            logger.warn("Permission {} already used by another role", perm);
            throw new IllegalArgumentException("Permission already used by another role");
        }
        logger.info("Creating role with permission {}", perm);
        try {
            InputOutputMapper<RoleInputDTO, Role, RoleOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoleInputDTO.class, Role.class, RoleOutputDTO.class);
            Role role = io.convertFromInput(input);
            if (role.getName() == null || role.getName().isBlank()) {
                role.setName(perm.name());
            }
            role = roleRepository.save(role);
            RoleOutputDTO output = io.convertToOutput(role);
            logger.info("Created role {} with permission {}", role.getId(), perm);
            return output;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating role with permission {}", perm, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public RoleOutputDTO update(Long id, RoleInputDTO input) {
        logger.info("Updating role {} with input {}", id, input);
        try {
            InputOutputMapper<RoleInputDTO, Role, RoleOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoleInputDTO.class, Role.class, RoleOutputDTO.class);
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Role {} not found for update", id);
                        return new NoSuchElementException("Role not found");
                    });
            if (input.getPermission() != null && input.getPermission() != role.getPermission()) {
                Optional<Role> exists = roleRepository.findByPermission(input.getPermission());
                if (exists.isPresent() && !exists.get().getId().equals(id)) {
                    logger.warn("Permission {} already used by role {}", input.getPermission(), exists.get().getId());
                    throw new IllegalArgumentException("Permission already used by another role");
                }
                role.setPermission(input.getPermission());
            }
            if (input.getName() != null) {
                role.setName(input.getName());
            }
            role = roleRepository.save(role);
            RoleOutputDTO output = io.convertToOutput(role);
            logger.info("Updated role {} successfully", id);
            return output;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating role {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting role {}", id);
        try {
            if (!roleRepository.existsById(id)) {
                logger.warn("Role {} not found for deletion", id);
                return false;
            }
            roleRepository.deleteById(id);
            logger.info("Deleted role {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting role {}", id, ex);
            throw ex;
        }
    }
}
