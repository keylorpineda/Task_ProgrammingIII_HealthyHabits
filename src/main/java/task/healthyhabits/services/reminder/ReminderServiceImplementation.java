package task.healthyhabits.services.reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Reminder;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ReminderRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImplementation implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderDTO> list(Pageable pageable) {
        return reminderRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(Reminder.class, ReminderDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderDTO> myReminders(Long userId, Pageable pageable) {
        List<ReminderDTO> filtered = reminderRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId() != null && r.getUser().getId().equals(userId))
                .map(r -> mapperFactory.createMapper(Reminder.class, ReminderDTO.class).convertToDTO(r))
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderDTO findByIdOrNull(Long id) {
        return reminderRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(Reminder.class, ReminderDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public ReminderOutputDTO create(ReminderInputDTO input) {
        InputOutputMapper<ReminderInputDTO, Reminder, ReminderOutputDTO> io =
                mapperFactory.createInputOutputMapper(ReminderInputDTO.class, Reminder.class, ReminderOutputDTO.class);
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setHabit(habit);
        reminder.setTime(input.getTime());
        reminder.setFrequency(input.getFrequency());
        reminder = reminderRepository.save(reminder);
        return io.convertToOutput(reminder);
    }

    @Override
    @Transactional
    public ReminderOutputDTO update(Long id, ReminderInputDTO input) {
        InputOutputMapper<ReminderInputDTO, Reminder, ReminderOutputDTO> io =
                mapperFactory.createInputOutputMapper(ReminderInputDTO.class, Reminder.class, ReminderOutputDTO.class);
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found"));
        if (input.getUserId() != null) {
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            reminder.setUser(user);
        }
        if (input.getHabitId() != null) {
            Habit habit = habitRepository.findById(input.getHabitId())
                    .orElseThrow(() -> new NoSuchElementException("Habit not found"));
            reminder.setHabit(habit);
        }
        if (input.getTime() != null) reminder.setTime(input.getTime());
        if (input.getFrequency() != null) reminder.setFrequency(input.getFrequency());
        reminder = reminderRepository.save(reminder);
        return io.convertToOutput(reminder);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!reminderRepository.existsById(id)) return false;
        reminderRepository.deleteById(id);
        return true;
    }

    private static <T> Page<T> paginate(List<T> all, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        if (offset >= all.size()) return new PageImpl<>(new ArrayList<>(), pageable, all.size());
        List<T> content = all.stream().skip(offset).limit(size).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, all.size());
    }
}
