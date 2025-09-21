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
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.routine.RoutineServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceImplementationTest {

    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private RoutineActivityRepository routineActivityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<Routine, RoutineDTO> routineMapper;
    @Mock
    private InputOutputMapper<RoutineInputDTO, Routine, RoutineOutputDTO> ioMapper;

    @InjectMocks
    private RoutineServiceImplementation service;

    private Routine routine;
    private RoutineDTO routineDTO;
    private RoutineOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        routine = new Routine();
        routine.setId(6L);
        routine.setActivities(new ArrayList<>());
        routineDTO = new RoutineDTO();
        routineDTO.setId(6L);
        outputDTO = new RoutineOutputDTO();
        when(mapperFactory.createMapper(Routine.class, RoutineDTO.class)).thenReturn(routineMapper);
        when(mapperFactory.createInputOutputMapper(RoutineInputDTO.class, Routine.class, RoutineOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 2);
        when(routineRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(routine), pageable, 1));
        when(routineMapper.convertToDTO(routine)).thenReturn(routineDTO);

        Page<RoutineDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(routineDTO, page.getContent().getFirst());
    }

    @Test
    void create_linksActivitiesAndSavesAll() {
        RoutineActivityInputDTO activityInput = new RoutineActivityInputDTO(2L, 30, 15, "notes");
        RoutineInputDTO input = new RoutineInputDTO("Morning", "desc", List.of("tag"),
                List.of(DaysOfWeek.MONDAY), 1L, List.of(activityInput));
        User user = new User();
        Habit habit = new Habit();
        habit.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));
        Routine[] savedHolder = new Routine[1];
        when(routineRepository.save(any(Routine.class))).thenAnswer(invocation -> {
            Routine saved = invocation.getArgument(0);
            saved.setId(6L);
            savedHolder[0] = saved;
            return saved;
        });
        when(ioMapper.convertToOutput(any(Routine.class))).thenReturn(outputDTO);

        RoutineOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        ArgumentCaptor<List<RoutineActivity>> captor = ArgumentCaptor.forClass(List.class);
        verify(routineActivityRepository).saveAll(captor.capture());
        List<RoutineActivity> savedActivities = captor.getValue();
        assertEquals(1, savedActivities.size());
        RoutineActivity savedActivity = savedActivities.getFirst();
        assertEquals(habit, savedActivity.getHabit());
        assertEquals(savedHolder[0], savedActivity.getRoutine());
        assertEquals(savedActivities, savedHolder[0].getActivities());
        verify(ioMapper).convertToOutput(savedHolder[0]);
    }

    @Test
    void update_replacesActivitiesAndClearsPreviousOnes() {
        RoutineActivity existing = new RoutineActivity();
        routine.getActivities().add(existing);
        RoutineActivityInputDTO activityInput = new RoutineActivityInputDTO(4L, 20, 10, "new");
        RoutineInputDTO input = new RoutineInputDTO("Title", "desc", List.of(),
                List.of(DaysOfWeek.TUESDAY), 3L, List.of(activityInput));
        User user = new User();
        Habit habit = new Habit();
        habit.setId(4L);
        when(routineRepository.findById(6L)).thenReturn(Optional.of(routine));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(habitRepository.findById(4L)).thenReturn(Optional.of(habit));
        when(routineRepository.save(routine)).thenReturn(routine);
        when(ioMapper.convertToOutput(routine)).thenReturn(outputDTO);

        RoutineOutputDTO result = service.update(6L, input);

        assertSame(outputDTO, result);
        ArgumentCaptor<List<RoutineActivity>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        verify(routineActivityRepository).deleteAll(deleteCaptor.capture());
        assertTrue(deleteCaptor.getValue().contains(existing));
        verify(routineActivityRepository).saveAll(anyList());
        assertEquals(user, routine.getUser());
        assertEquals(1, routine.getActivities().size());
        assertEquals(routine, routine.getActivities().getFirst().getRoutine());
    }

    @Test
    void delete_removesRoutineAndActivities() {
        RoutineActivity activity = new RoutineActivity();
        routine.setActivities(List.of(activity));
        when(routineRepository.findById(6L)).thenReturn(Optional.of(routine));

        assertTrue(service.delete(6L));
        verify(routineActivityRepository).deleteAll(anyList());
        verify(routineRepository).deleteById(6L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(routineRepository.findById(9L)).thenReturn(Optional.empty());

        assertFalse(service.delete(9L));
    }
}
