package task.healthyhabits.services.progressLog;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProgressLogServiceImplementation implements ProgressLogService {

    private static final Logger logger = LogManager.getLogger(ProgressLogServiceImplementation.class);
    private final ProgressLogRepository progressLogRepository;
    private final CompletedActivityRepository completedActivityRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> list(Pageable pageable) {
        logger.info("Listing progress logs with pageable {}", pageable);
        try {
            Page<ProgressLogDTO> logs = progressLogRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class)
                            .convertToDTO(entity));
            logger.info("Listed {} progress logs", logs.getNumberOfElements());
            return logs;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing progress logs with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressLogDTO findByIdOrNull(Long id) {
        return progressLogRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressLogDTO byDate(Long userId, LocalDate date) {
        return progressLogRepository.findByUserIdAndDate(userId, date)
                .map(entity -> mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> byRange(Long userId, LocalDate from, LocalDate to, Pageable pageable) {
         return progressLogRepository.findAllByUserIdAndDateBetween(userId, from, to, pageable)
                .map(entity -> mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional
    public ProgressLogOutputDTO create(ProgressLogInputDTO input) {
        logger.info("Creating progress log for user {} and routine {}", input.getUserId(), input.getRoutineId());
        try {
            InputOutputMapper<ProgressLogInputDTO, ProgressLog, ProgressLogOutputDTO> io = mapperFactory
                    .createInputOutputMapper(ProgressLogInputDTO.class, ProgressLog.class, ProgressLogOutputDTO.class);
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for progress log creation", input.getUserId());
                        return new NoSuchElementException("User not found");
                    });
            Routine routine = routineRepository.findById(input.getRoutineId())
                    .orElseThrow(() -> {
                        logger.warn("Routine {} not found for progress log creation", input.getRoutineId());
                        return new NoSuchElementException("Routine not found");
                    });
            ProgressLog progressLog = new ProgressLog();
            progressLog.setUser(user);
            progressLog.setRoutine(routine);
            progressLog.setDate(input.getDate());
            progressLog = progressLogRepository.save(progressLog);
            if (input.getCompletedActivityInputs() != null) {
                List<CompletedActivity> list = new ArrayList<>();
                for (CompletedActivityInputDTO caIn : input.getCompletedActivityInputs()) {
                    Habit habit = habitRepository.findById(caIn.getHabitId())
                            .orElseThrow(() -> {
                                logger.warn("Habit {} not found for completed activity creation", caIn.getHabitId());
                                return new NoSuchElementException("Habit not found");
                            });
                    CompletedActivity ca = new CompletedActivity();
                    ca.setHabit(habit);
                    ca.setCompletedAt(caIn.getCompletedAt());
                    ca.setNotes(caIn.getNotes());
                    ca.setProgressLog(progressLog);
                    list.add(ca);
                }
                completedActivityRepository.saveAll(list);
                progressLog.setCompletedActivities(list);
            }
            ProgressLogOutputDTO output = io.convertToOutput(progressLog);
            logger.info("Created progress log {} for user {}", progressLog.getId(), user.getId());
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating progress log for user {}", input.getUserId(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public ProgressLogOutputDTO update(Long id, ProgressLogInputDTO input) {
        logger.info("Updating progress log {} with input {}", id, input);
        try {
            InputOutputMapper<ProgressLogInputDTO, ProgressLog, ProgressLogOutputDTO> io = mapperFactory
                    .createInputOutputMapper(ProgressLogInputDTO.class, ProgressLog.class, ProgressLogOutputDTO.class);
            ProgressLog progressLog = progressLogRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Progress log {} not found for update", id);
                        return new NoSuchElementException("ProgressLog not found");
                    });
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for progress log update", input.getUserId());
                        return new NoSuchElementException("User not found");
                    });
            Routine routine = routineRepository.findById(input.getRoutineId())
                    .orElseThrow(() -> {
                        logger.warn("Routine {} not found for progress log update", input.getRoutineId());
                        return new NoSuchElementException("Routine not found");
                    });
            progressLog.setUser(user);
            progressLog.setRoutine(routine);
            progressLog.setDate(input.getDate());
            if (progressLog.getCompletedActivities() != null && !progressLog.getCompletedActivities().isEmpty()) {
                completedActivityRepository.deleteAll(new ArrayList<>(progressLog.getCompletedActivities()));
                progressLog.getCompletedActivities().clear();
            }
            if (input.getCompletedActivityInputs() != null) {
                List<CompletedActivity> list = new ArrayList<>();
                for (CompletedActivityInputDTO caIn : input.getCompletedActivityInputs()) {
                    Habit habit = habitRepository.findById(caIn.getHabitId())
                            .orElseThrow(() -> {
                                logger.warn("Habit {} not found for completed activity update", caIn.getHabitId());
                                return new NoSuchElementException("Habit not found");
                            });
                    CompletedActivity ca = new CompletedActivity();
                    ca.setHabit(habit);
                    ca.setCompletedAt(caIn.getCompletedAt());
                    ca.setNotes(caIn.getNotes());
                    ca.setProgressLog(progressLog);
                    list.add(ca);
                }
                completedActivityRepository.saveAll(list);
                progressLog.setCompletedActivities(list);
            }
            progressLog = progressLogRepository.save(progressLog);
            ProgressLogOutputDTO output = io.convertToOutput(progressLog);
            logger.info("Updated progress log {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating progress log {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting progress log {}", id);
        try {
            ProgressLog progressLog = progressLogRepository.findById(id).orElse(null);
            if (progressLog == null) {
                logger.warn("Progress log {} not found for deletion", id);
                return false;
            }
            if (progressLog.getCompletedActivities() != null && !progressLog.getCompletedActivities().isEmpty()) {
                completedActivityRepository.deleteAll(new ArrayList<>(progressLog.getCompletedActivities()));
            }
            progressLogRepository.deleteById(id);
            logger.info("Deleted progress log {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting progress log {}", id, ex);
            throw ex;
        }
    }
}
