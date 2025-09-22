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
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.models.Frequency;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Reminder;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ReminderRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.reminder.ReminderServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceImplementationTest {

    @Mock
    private ReminderRepository reminderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<Reminder, ReminderDTO> reminderMapper;
    @Mock
    private InputOutputMapper<ReminderInputDTO, Reminder, ReminderOutputDTO> ioMapper;

    @InjectMocks
    private ReminderServiceImplementation service;

    private Reminder reminder;
    private ReminderDTO reminderDTO;
    private ReminderOutputDTO outputDTO;

    @BeforeEach
    void setUp() {
        reminder = new Reminder();
        reminder.setId(5L);
        reminderDTO = new ReminderDTO();
        reminderDTO.setId(5L);
        outputDTO = new ReminderOutputDTO();
        lenient().when(mapperFactory.createMapper(Reminder.class, ReminderDTO.class)).thenReturn(reminderMapper);
        lenient().when(mapperFactory.createInputOutputMapper(ReminderInputDTO.class, Reminder.class, ReminderOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 2);
        when(reminderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(reminder), pageable, 1));
        when(reminderMapper.convertToDTO(reminder)).thenReturn(reminderDTO);

        Page<ReminderDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(reminderDTO, page.getContent().getFirst());
    }

    @Test
    void myReminders_fetchesFromRepositoryAndMaps() {
        PageRequest pageable = PageRequest.of(0, 1);
        when(reminderRepository.findAllByUserId(2L, pageable))
                .thenReturn(new PageImpl<>(List.of(reminder), pageable, 1));
        when(reminderMapper.convertToDTO(reminder)).thenReturn(reminderDTO);

        Page<ReminderDTO> page = service.myReminders(2L, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(reminderDTO, page.getContent().getFirst());
        verify(reminderRepository).findAllByUserId(2L, pageable);
        verify(reminderMapper).convertToDTO(reminder);
    }

    @Test
    void myReminders_outOfBoundsPageIsEmpty() {
        PageRequest pageable = PageRequest.of(1, 1);
        when(reminderRepository.findAllByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 1));

        Page<ReminderDTO> page = service.myReminders(1L, pageable);

        assertTrue(page.isEmpty());
    }

    @Test
    void create_mapsInputAndPersists() {
        ReminderInputDTO input = new ReminderInputDTO(1L, 2L, LocalTime.NOON, Frequency.DAILY);
        User user = new User();
        Habit habit = new Habit();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(invocation -> {
            Reminder r = invocation.getArgument(0);
            r.setId(5L);
            return r;
        });
        when(ioMapper.convertToOutput(any(Reminder.class))).thenReturn(outputDTO);

        ReminderOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(captor.capture());
        Reminder saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals(habit, saved.getHabit());
        assertEquals(LocalTime.NOON, saved.getTime());
        assertEquals(Frequency.DAILY, saved.getFrequency());
    }

    @Test
    void update_updatesMutableFields() {
        ReminderInputDTO input = new ReminderInputDTO(1L, 2L, LocalTime.MIDNIGHT, Frequency.WEEKLY);
        reminder.setUser(new User());
        reminder.setHabit(new Habit());
        when(reminderRepository.findById(5L)).thenReturn(Optional.of(reminder));
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Habit habit = new Habit();
        when(habitRepository.findById(2L)).thenReturn(Optional.of(habit));
        when(reminderRepository.save(reminder)).thenReturn(reminder);
        when(ioMapper.convertToOutput(reminder)).thenReturn(outputDTO);

        ReminderOutputDTO result = service.update(5L, input);

        assertSame(outputDTO, result);
        assertEquals(user, reminder.getUser());
        assertEquals(habit, reminder.getHabit());
        assertEquals(LocalTime.MIDNIGHT, reminder.getTime());
        assertEquals(Frequency.WEEKLY, reminder.getFrequency());
    }

    @Test
    void delete_removesWhenExists() {
        when(reminderRepository.existsById(5L)).thenReturn(true);

        assertTrue(service.delete(5L));
        verify(reminderRepository).deleteById(5L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(reminderRepository.existsById(8L)).thenReturn(false);

        assertFalse(service.delete(8L));
    }
}