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
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Habit;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.services.habit.HabitServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceImplementationTest {

    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<Habit, HabitDTO> habitMapper;
    @Mock
    private InputOutputMapper<HabitInputDTO, Habit, HabitOutputDTO> ioMapper;

    @InjectMocks
    private HabitServiceImplementation service;

    private Habit habit;
    private HabitDTO habitDTO;
    private HabitOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        habit = new Habit();
        habit.setId(1L);
        habit.setName("Drink water");
        habitDTO = new HabitDTO();
        habitDTO.setId(1L);
        outputDTO = new HabitOutputDTO();
        when(mapperFactory.createMapper(Habit.class, HabitDTO.class)).thenReturn(habitMapper);
        when(mapperFactory.createInputOutputMapper(HabitInputDTO.class, Habit.class, HabitOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(habitRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(habit), pageable, 1));
        when(habitMapper.convertToDTO(habit)).thenReturn(habitDTO);

        Page<HabitDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(habitDTO, page.getContent().getFirst());
        verify(habitMapper).convertToDTO(habit);
    }

    @Test
    void create_convertsInputAndReturnsOutput() {
        HabitInputDTO input = new HabitInputDTO("Read", Category.MINDFULNESS, "desc");
        when(ioMapper.convertFromInput(input)).thenReturn(habit);
        when(habitRepository.save(habit)).thenReturn(habit);
        when(ioMapper.convertToOutput(habit)).thenReturn(outputDTO);

        HabitOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        verify(habitRepository).save(habit);
    }

    @Test
    void update_appliesProvidedValues() {
        HabitInputDTO input = new HabitInputDTO("Jog", Category.FITNESS, "cardio");
        habit.setCategory(Category.MINDFULNESS);
        habit.setDescription("old");
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);
        when(ioMapper.convertToOutput(habit)).thenReturn(outputDTO);

        HabitOutputDTO result = service.update(1L, input);

        assertSame(outputDTO, result);
        assertEquals("Jog", habit.getName());
        assertEquals(Category.FITNESS, habit.getCategory());
        assertEquals("cardio", habit.getDescription());
    }

    @Test
    void delete_whenExistsReturnsTrue() {
        when(habitRepository.existsById(1L)).thenReturn(true);

        assertTrue(service.delete(1L));
        verify(habitRepository).deleteById(1L);
    }

    @Test
    void delete_whenMissingReturnsFalse() {
        when(habitRepository.existsById(2L)).thenReturn(false);

        assertFalse(service.delete(2L));
        verify(habitRepository, never()).deleteById(anyLong());
    }
}
