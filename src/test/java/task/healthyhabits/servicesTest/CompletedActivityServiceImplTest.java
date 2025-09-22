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
import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ProgressLogRepository;
import task.healthyhabits.services.completedActivity.CompletedActivityServiceImpl;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletedActivityServiceImplTest {

    @Mock
    private CompletedActivityRepository completedActivityRepository;
    @Mock
    private ProgressLogRepository progressLogRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<CompletedActivity, CompletedActivityDTO> completedActivityMapper;
    @Mock
    private InputOutputMapper<CompletedActivityInputDTO, CompletedActivity, CompletedActivityOutputDTO> ioMapper;

    @InjectMocks
    private CompletedActivityServiceImpl service;

    private CompletedActivity completedActivity;
    private CompletedActivityDTO completedActivityDTO;
    private CompletedActivityOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        completedActivity = new CompletedActivity();
        completedActivity.setId(10L);
        completedActivityDTO = new CompletedActivityDTO();
        completedActivityDTO.setId(10L);
        outputDTO = new CompletedActivityOutputDTO();
        lenient().when(mapperFactory.createMapper(CompletedActivity.class, CompletedActivityDTO.class))
                .thenReturn(completedActivityMapper);
        lenient().when(mapperFactory.createInputOutputMapper(CompletedActivityInputDTO.class, CompletedActivity.class,
                CompletedActivityOutputDTO.class)).thenReturn(ioMapper);
    }

    @Test
    void list_usesMapperForEachElement() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(completedActivityRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(completedActivity), pageable, 1));
        when(completedActivityMapper.convertToDTO(completedActivity)).thenReturn(completedActivityDTO);

        Page<CompletedActivityDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(completedActivityDTO, page.getContent().getFirst());
        verify(completedActivityMapper).convertToDTO(completedActivity);
    }

    @Test
    void findByIdOrNull_returnsDtoWhenPresent() {
        when(completedActivityRepository.findById(10L)).thenReturn(Optional.of(completedActivity));
        when(completedActivityMapper.convertToDTO(completedActivity)).thenReturn(completedActivityDTO);

        CompletedActivityDTO result = service.findByIdOrNull(10L);

        assertSame(completedActivityDTO, result);
    }

    @Test
    void findByIdOrNull_returnsNullWhenMissing() {
        when(completedActivityRepository.findById(5L)).thenReturn(Optional.empty());

        assertNull(service.findByIdOrNull(5L));
    }

    @Test
    void create_mapsInputAndPersistsWithRelations() {
        CompletedActivityInputDTO input = new CompletedActivityInputDTO(2L, OffsetDateTime.now(), "notes");
        ProgressLog progressLog = new ProgressLog();
        progressLog.setId(4L);
        Habit habit = new Habit();
        habit.setId(2L);
        when(progressLogRepository.findById(7L)).thenReturn(Optional.of(progressLog));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));
        when(completedActivityRepository.save(any(CompletedActivity.class))).thenAnswer(invocation -> {
            CompletedActivity ca = invocation.getArgument(0);
            ca.setId(11L);
            return ca;
        });
        when(ioMapper.convertToOutput(any(CompletedActivity.class))).thenReturn(outputDTO);

        CompletedActivityOutputDTO result = service.create(7L, input);

        assertSame(outputDTO, result);
        ArgumentCaptor<CompletedActivity> captor = ArgumentCaptor.forClass(CompletedActivity.class);
        verify(completedActivityRepository).save(captor.capture());
        CompletedActivity persisted = captor.getValue();
        assertEquals(progressLog, persisted.getProgressLog());
        assertEquals(habit, persisted.getHabit());
        assertEquals(input.getCompletedAt(), persisted.getCompletedAt());
        assertEquals(input.getNotes(), persisted.getNotes());
        verify(ioMapper).convertToOutput(persisted);
    }

    @Test
    void update_mapsChangesAndPersists() {
        CompletedActivityInputDTO input = new CompletedActivityInputDTO(3L, OffsetDateTime.now(), "new");
        Habit newHabit = new Habit();
        newHabit.setId(3L);
        when(completedActivityRepository.findById(10L)).thenReturn(Optional.of(completedActivity));
        when(habitRepository.findById(3L)).thenReturn(Optional.of(newHabit));
        when(completedActivityRepository.save(completedActivity)).thenReturn(completedActivity);
        when(ioMapper.convertToOutput(completedActivity)).thenReturn(outputDTO);

        CompletedActivityOutputDTO result = service.update(10L, input);

        assertSame(outputDTO, result);
        assertEquals(newHabit, completedActivity.getHabit());
        assertEquals(input.getCompletedAt(), completedActivity.getCompletedAt());
        assertEquals("new", completedActivity.getNotes());
        verify(completedActivityRepository).save(completedActivity);
    }

    @Test
    void delete_removesWhenExists() {
        when(completedActivityRepository.existsById(10L)).thenReturn(true);

        assertTrue(service.delete(10L));
        verify(completedActivityRepository).deleteById(10L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(completedActivityRepository.existsById(8L)).thenReturn(false);

        assertFalse(service.delete(8L));
        verify(completedActivityRepository, never()).deleteById(anyLong());
    }
}