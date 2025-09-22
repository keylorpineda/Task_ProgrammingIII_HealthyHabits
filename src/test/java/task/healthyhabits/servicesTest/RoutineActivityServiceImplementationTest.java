package task.healthyhabits.servicesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.services.routineActivity.RoutineActivityServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineActivityServiceImplementationTest {

    @Mock
    private RoutineActivityRepository routineActivityRepository;
    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<RoutineActivity, RoutineActivityDTO> activityMapper;
    @Mock
    private InputOutputMapper<RoutineActivityInputDTO, RoutineActivity, RoutineActivityOutputDTO> ioMapper;

    @InjectMocks
    private RoutineActivityServiceImplementation service;

    private RoutineActivity activity;
    private RoutineActivityDTO activityDTO;
    private RoutineActivityOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        activity = new RoutineActivity();
        activity.setId(3L);
        activityDTO = new RoutineActivityDTO();
        activityDTO.setId(3L);
        outputDTO = new RoutineActivityOutputDTO();
        lenient().when(mapperFactory.createMapper(RoutineActivity.class, RoutineActivityDTO.class)).thenReturn(activityMapper);
        lenient().when(mapperFactory.createInputOutputMapper(RoutineActivityInputDTO.class, RoutineActivity.class,
                RoutineActivityOutputDTO.class)).thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(routineActivityRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(activity), pageable, 1));
        when(activityMapper.convertToDTO(activity)).thenReturn(activityDTO);

        Page<RoutineActivityDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(activityDTO, page.getContent().getFirst());
    }

    @Test
    void create_setsRelationshipsAndPersists() {
        RoutineActivityInputDTO input = new RoutineActivityInputDTO(2L, 45, 10, "notes");
        Routine routine = new Routine();
        Habit habit = new Habit();
        when(routineRepository.findById(5L)).thenReturn(Optional.of(routine));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));
        when(routineActivityRepository.save(any(RoutineActivity.class))).thenAnswer(invocation -> {
            RoutineActivity ra = invocation.getArgument(0);
            ra.setId(3L);
            return ra;
        });
        when(ioMapper.convertToOutput(any(RoutineActivity.class))).thenReturn(outputDTO);

        RoutineActivityOutputDTO result = service.create(5L, input);

        assertSame(outputDTO, result);
        ArgumentCaptor<RoutineActivity> captor = ArgumentCaptor.forClass(RoutineActivity.class);
        verify(routineActivityRepository).save(captor.capture());
        RoutineActivity saved = captor.getValue();
        assertEquals(routine, saved.getRoutine());
        assertEquals(habit, saved.getHabit());
        assertEquals(45, saved.getDuration());
        assertEquals(10, saved.getTargetTime());
        assertEquals("notes", saved.getNotes());
    }

    @Test
    void update_updatesFieldsAndPersists() {
        RoutineActivityInputDTO input = new RoutineActivityInputDTO(7L, 20, 5, "new");
        activity.setRoutine(new Routine());
        activity.setHabit(new Habit());
        when(routineActivityRepository.findById(3L)).thenReturn(Optional.of(activity));
        Habit habit = new Habit();
        when(habitRepository.findById(7L)).thenReturn(Optional.of(habit));
        when(routineActivityRepository.save(activity)).thenReturn(activity);
        when(ioMapper.convertToOutput(activity)).thenReturn(outputDTO);

        RoutineActivityOutputDTO result = service.update(3L, input);

        assertSame(outputDTO, result);
        assertEquals(habit, activity.getHabit());
        assertEquals(20, activity.getDuration());
        assertEquals(5, activity.getTargetTime());
        assertEquals("new", activity.getNotes());
    }

    @Test
    void delete_returnsTrueWhenExists() {
        when(routineActivityRepository.existsById(3L)).thenReturn(true);

        assertTrue(service.delete(3L));
        verify(routineActivityRepository).deleteById(3L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(routineActivityRepository.existsById(2L)).thenReturn(false);

        assertFalse(service.delete(2L));
    }
}