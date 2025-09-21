package task.healthyhabits.services.reminder;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
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
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReminderServiceImplementation implements ReminderService {

    private static final Logger logger = LogManager.getLogger(ReminderServiceImplementation.class);
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderDTO> list(Pageable pageable) {
        logger.info("Listing reminders with pageable {}", pageable);
        try {
            Page<ReminderDTO> reminders = reminderRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(Reminder.class, ReminderDTO.class).convertToDTO(entity));
            logger.info("Listed {} reminders", reminders.getNumberOfElements());
            return reminders;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing reminders with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderDTO> myReminders(Long userId, Pageable pageable) {
        GenericMapper<Reminder, ReminderDTO> mapper = mapperFactory.createMapper(Reminder.class, ReminderDTO.class);
        return reminderRepository.findAllByUserId(userId, pageable)
                .map(mapper::convertToDTO);
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
        logger.info("Creating reminder for user {} and habit {}", input.getUserId(), input.getHabitId());
        try {
            InputOutputMapper<ReminderInputDTO, Reminder, ReminderOutputDTO> io = mapperFactory
                    .createInputOutputMapper(ReminderInputDTO.class, Reminder.class, ReminderOutputDTO.class);
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for reminder creation", input.getUserId());
                        return new NoSuchElementException("User not found");
                    });
            Habit habit = habitRepository.findById(input.getHabitId())
                    .orElseThrow(() -> {
                        logger.warn("Habit {} not found for reminder creation", input.getHabitId());
                        return new NoSuchElementException("Habit not found");
                    });
            Reminder reminder = new Reminder();
            reminder.setUser(user);
            reminder.setHabit(habit);
            reminder.setTime(input.getTime());
            reminder.setFrequency(input.getFrequency());
            reminder = reminderRepository.save(reminder);
            ReminderOutputDTO output = io.convertToOutput(reminder);
            logger.info("Created reminder {} for user {}", reminder.getId(), user.getId());
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating reminder for user {}", input.getUserId(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public ReminderOutputDTO update(Long id, ReminderInputDTO input) {
        logger.info("Updating reminder {} with input {}", id, input);
        try {
            InputOutputMapper<ReminderInputDTO, Reminder, ReminderOutputDTO> io = mapperFactory
                    .createInputOutputMapper(ReminderInputDTO.class, Reminder.class, ReminderOutputDTO.class);
            Reminder reminder = reminderRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Reminder {} not found for update", id);
                        return new NoSuchElementException("Reminder not found");
                    });
            if (input.getUserId() != null) {
                User user = userRepository.findById(input.getUserId())
                        .orElseThrow(() -> {
                            logger.warn("User {} not found for reminder update", input.getUserId());
                            return new NoSuchElementException("User not found");
                        });
                reminder.setUser(user);
            }
            if (input.getHabitId() != null) {
                Habit habit = habitRepository.findById(input.getHabitId())
                        .orElseThrow(() -> {
                            logger.warn("Habit {} not found for reminder update", input.getHabitId());
                            return new NoSuchElementException("Habit not found");
                        });
                reminder.setHabit(habit);
            }
            if (input.getTime() != null)
                reminder.setTime(input.getTime());
            if (input.getFrequency() != null)
                reminder.setFrequency(input.getFrequency());
            reminder = reminderRepository.save(reminder);
            ReminderOutputDTO output = io.convertToOutput(reminder);
            logger.info("Updated reminder {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating reminder {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting reminder {}", id);
        try {
            if (!reminderRepository.existsById(id)) {
                logger.warn("Reminder {} not found for deletion", id);
                return false;
            }
            reminderRepository.deleteById(id);
            logger.info("Deleted reminder {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting reminder {}", id, ex);
            throw ex;
        }
    }
}
