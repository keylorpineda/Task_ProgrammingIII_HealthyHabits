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
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ProgressLogRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.progressLog.ProgressLogServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressLogServiceImplementationTest {

    @Mock
    private ProgressLogRepository progressLogRepository;
    @Mock
    private CompletedActivityRepository completedActivityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<ProgressLog, ProgressLogDTO> progressLogMapper;
    @Mock
    private InputOutputMapper<ProgressLogInputDTO, ProgressLog, ProgressLogOutputDTO> ioMapper;

    @InjectMocks
    private ProgressLogServiceImplementation service;

    private ProgressLog progressLog;
    private ProgressLogDTO progressLogDTO;
    private ProgressLogOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        progressLog = new ProgressLog();
        progressLog.setId(9L);
        progressLogDTO = new ProgressLogDTO();
        progressLogDTO.setId(9L);
        outputDTO = new ProgressLogOutputDTO();
        when(mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class)).thenReturn(progressLogMapper);
        when(mapperFactory.createInputOutputMapper(ProgressLogInputDTO.class, ProgressLog.class, ProgressLogOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(progressLogRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(progressLog), pageable, 1));
        when(progressLogMapper.convertToDTO(progressLog)).thenReturn(progressLogDTO);

        Page<ProgressLogDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(progressLogDTO, page.getContent().getFirst());
    }

    @Test
    void create_savesActivitiesAndLinksToProgressLog() {
        CompletedActivityInputDTO caInput = new CompletedActivityInputDTO(4L, OffsetDateTime.now(), "notes");
        ProgressLogInputDTO input = new ProgressLogInputDTO(2L, 3L, LocalDate.now(), List.of(caInput));
        User user = new User();
        user.setId(2L);
        Routine routine = new Routine();
        routine.setId(3L);
        Habit habit = new Habit();
        habit.setId(4L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(routineRepository.findById(3L)).thenReturn(Optional.of(routine));
        when(habitRepository.findById(4L)).thenReturn(Optional.of(habit));
        ProgressLog[] savedHolder = new ProgressLog[1];
        when(progressLogRepository.save(any(ProgressLog.class))).thenAnswer(invocation -> {
            ProgressLog saved = invocation.getArgument(0);
            saved.setId(9L);
            savedHolder[0] = saved;
            return saved;
        });
        when(ioMapper.convertToOutput(any(ProgressLog.class))).thenReturn(outputDTO);

        ProgressLogOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        ArgumentCaptor<List<CompletedActivity>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(completedActivityRepository).saveAll(listCaptor.capture());
        List<CompletedActivity> savedActs = listCaptor.getValue();
        assertEquals(1, savedActs.size());
        CompletedActivity created = savedActs.getFirst();
        assertEquals(habit, created.getHabit());
        assertEquals(savedHolder[0], created.getProgressLog());
        assertEquals(input.getDate(), created.getProgressLog().getDate());
        assertEquals("notes", created.getNotes());
        verify(ioMapper).convertToOutput(savedHolder[0]);
    }

    @Test
    void update_replacesActivitiesAndClearsPreviousList() {
        CompletedActivity existing = new CompletedActivity();
        existing.setId(1L);
        List<CompletedActivity> existingList = new ArrayList<>();
        existingList.add(existing);
        progressLog.setCompletedActivities(existingList);
        progressLog.setDate(LocalDate.now());
        ProgressLogInputDTO input = new ProgressLogInputDTO(2L, 3L, LocalDate.now().plusDays(1),
                List.of(new CompletedActivityInputDTO(5L, OffsetDateTime.now(), "new")));
        User user = new User();
        Routine routine = new Routine();
        Habit habit = new Habit();
        habit.setId(5L);
        when(progressLogRepository.findById(9L)).thenReturn(Optional.of(progressLog));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(routineRepository.findById(3L)).thenReturn(Optional.of(routine));
        when(habitRepository.findById(5L)).thenReturn(Optional.of(habit));
        when(progressLogRepository.save(progressLog)).thenReturn(progressLog);
        when(ioMapper.convertToOutput(progressLog)).thenReturn(outputDTO);

        ProgressLogOutputDTO result = service.update(9L, input);

        assertSame(outputDTO, result);
        ArgumentCaptor<List<CompletedActivity>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        verify(completedActivityRepository).deleteAll(deleteCaptor.capture());
        assertTrue(deleteCaptor.getValue().contains(existing));
        verify(completedActivityRepository).saveAll(anyList());
        assertEquals(user, progressLog.getUser());
        assertEquals(routine, progressLog.getRoutine());
        assertEquals(input.getDate(), progressLog.getDate());
        assertEquals(1, progressLog.getCompletedActivities().size());
        assertEquals(progressLog, progressLog.getCompletedActivities().getFirst().getProgressLog());
    }

    @Test
    void delete_removesProgressLogAndActivities() {
        CompletedActivity existing = new CompletedActivity();
        progressLog.setCompletedActivities(List.of(existing));
        when(progressLogRepository.findById(9L)).thenReturn(Optional.of(progressLog));

        assertTrue(service.delete(9L));
        verify(completedActivityRepository).deleteAll(anyList());
        verify(progressLogRepository).deleteById(9L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(progressLogRepository.findById(5L)).thenReturn(Optional.empty());

        assertFalse(service.delete(5L));
        verify(completedActivityRepository, never()).deleteAll(anyList());
    }
}
