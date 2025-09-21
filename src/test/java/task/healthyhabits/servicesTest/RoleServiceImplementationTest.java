package task.healthyhabits.servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.services.role.RoleServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplementationTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<Role, RoleDTO> roleMapper;
    @Mock
    private InputOutputMapper<RoleInputDTO, Role, RoleOutputDTO> ioMapper;

    @InjectMocks
    private RoleServiceImplementation service;

    private Role role;
    private RoleDTO roleDTO;
    private RoleOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(4L);
        role.setPermission(Permission.USER_READ);
        roleDTO = new RoleDTO();
        roleDTO.setId(4L);
        outputDTO = new RoleOutputDTO();
        when(mapperFactory.createMapper(Role.class, RoleDTO.class)).thenReturn(roleMapper);
        when(mapperFactory.createInputOutputMapper(RoleInputDTO.class, Role.class, RoleOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 2);
        when(roleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(role), pageable, 1));
        when(roleMapper.convertToDTO(role)).thenReturn(roleDTO);

        Page<RoleDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(roleDTO, page.getContent().getFirst());
    }

    @Test
    void create_setsNameWhenMissingAndReturnsOutput() {
        RoleInputDTO input = new RoleInputDTO(null, Permission.USER_READ);
        when(roleRepository.findByPermission(Permission.USER_READ)).thenReturn(Optional.empty());
        when(ioMapper.convertFromInput(input)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(ioMapper.convertToOutput(role)).thenReturn(outputDTO);

        RoleOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        assertEquals(Permission.USER_READ.name(), role.getName());
        verify(roleRepository).save(role);
    }

    @Test
    void create_duplicatePermissionThrows() {
        when(roleRepository.findByPermission(Permission.USER_READ)).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(new RoleInputDTO("name", Permission.USER_READ)));
    }

    @Test
    void update_changesPermissionAndName() {
        RoleInputDTO input = new RoleInputDTO("new", Permission.USER_WRITE);
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));
        when(roleRepository.findByPermission(Permission.USER_WRITE)).thenReturn(Optional.empty());
        when(roleRepository.save(role)).thenReturn(role);
        when(ioMapper.convertToOutput(role)).thenReturn(outputDTO);

        RoleOutputDTO result = service.update(4L, input);

        assertSame(outputDTO, result);
        assertEquals("new", role.getName());
        assertEquals(Permission.USER_WRITE, role.getPermission());
    }

    @Test
    void update_rejectsDuplicatePermission() {
        RoleInputDTO input = new RoleInputDTO("new", Permission.USER_WRITE);
        Role other = new Role();
        other.setId(8L);
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));
        when(roleRepository.findByPermission(Permission.USER_WRITE)).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class, () -> service.update(4L, input));
    }

    @Test
    void delete_returnsTrueWhenExists() {
        when(roleRepository.existsById(4L)).thenReturn(true);

        assertTrue(service.delete(4L));
        verify(roleRepository).deleteById(4L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(roleRepository.existsById(7L)).thenReturn(false);

        assertFalse(service.delete(7L));
    }
}