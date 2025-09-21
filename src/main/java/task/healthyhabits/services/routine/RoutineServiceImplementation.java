package task.healthyhabits.services.routine;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoutineServiceImplementation implements RoutineService {

    private static final Logger logger = LogManager.getLogger(RoutineServiceImplementation.class);
    private final RoutineRepository routineRepository;
    private final RoutineActivityRepository routineActivityRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> list(Pageable pageable) {
        logger.info("Listing routines with pageable {}", pageable);
        try {
            Page<RoutineDTO> routines = routineRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(Routine.class, RoutineDTO.class).convertToDTO(entity));
            logger.info("Listed {} routines", routines.getNumberOfElements());
            return routines;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing routines with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> myRoutines(Long userId, Pageable pageable) {
        return routineRepository.findAllByUserId(userId, pageable)
                .map(r -> mapperFactory.createMapper(Routine.class, RoutineDTO.class).convertToDTO(r));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> byUser(Long userId, Pageable pageable) {
        return myRoutines(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineDTO findByIdOrNull(Long id) {
        return routineRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(Routine.class, RoutineDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public RoutineOutputDTO create(RoutineInputDTO input) {
        logger.info("Creating routine with input {}", input);
        try {
            InputOutputMapper<RoutineInputDTO, Routine, RoutineOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoutineInputDTO.class, Routine.class, RoutineOutputDTO.class);
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for routine creation", input.getUserId());
                        return new NoSuchElementException("User not found");
                    });
            Routine routine = new Routine();
            routine.setTitle(input.getTitle());
            routine.setDescription(input.getDescription());
            routine.setDaysOfWeek(input.getDaysOfWeek());
            routine.setTags(input.getTags() == null ? new ArrayList<>() : input.getTags());
            routine.setUser(user);
            routine.setActivities(new ArrayList<>());
            routine = routineRepository.save(routine);
            if (input.getActivityInputs() != null) {
                List<RoutineActivity> acts = new ArrayList<>();
                for (RoutineActivityInputDTO ai : input.getActivityInputs()) {
                    Habit habit = habitRepository.findById(ai.getHabitId())
                            .orElseThrow(() -> {
                                logger.warn("Habit {} not found for routine creation", ai.getHabitId());
                                return new NoSuchElementException("Habit not found");
                            });
                    RoutineActivity ra = new RoutineActivity();
                    ra.setHabit(habit);
                    ra.setDuration(ai.getDuration());
                    ra.setTargetTime(ai.getTargetTime() != null ? ai.getTargetTime() : 0);
                    ra.setNotes(ai.getNotes());
                    ra.setRoutine(routine);
                    acts.add(ra);
                }
                routineActivityRepository.saveAll(acts);
                routine.setActivities(acts);
            }
            RoutineOutputDTO output = io.convertToOutput(routine);
            logger.info("Created routine {} for user {}", routine.getId(), user.getId());
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating routine with input {}", input, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public RoutineOutputDTO update(Long id, RoutineInputDTO input) {
        logger.info("Updating routine {} with input {}", id, input);
        try {
            InputOutputMapper<RoutineInputDTO, Routine, RoutineOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoutineInputDTO.class, Routine.class, RoutineOutputDTO.class);
            Routine routine = routineRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Routine {} not found for update", id);
                        return new NoSuchElementException("Routine not found");
                    });
            User user = userRepository.findById(input.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for routine update", input.getUserId());
                        return new NoSuchElementException("User not found");
                    });
            if (input.getTitle() != null) routine.setTitle(input.getTitle());
            if (input.getDescription() != null) routine.setDescription(input.getDescription());
            if (input.getTags() != null) routine.setTags(input.getTags());
            if (input.getDaysOfWeek() != null) routine.setDaysOfWeek(input.getDaysOfWeek());
            routine.setUser(user);
            if (routine.getActivities() != null && !routine.getActivities().isEmpty()) {
                routineActivityRepository.deleteAll(new ArrayList<>(routine.getActivities()));
                routine.getActivities().clear();
            }
            if (input.getActivityInputs() != null) {
                List<RoutineActivity> acts = new ArrayList<>();
                for (RoutineActivityInputDTO ai : input.getActivityInputs()) {
                    Habit habit = habitRepository.findById(ai.getHabitId())
                            .orElseThrow(() -> {
                                logger.warn("Habit {} not found for routine update", ai.getHabitId());
                                return new NoSuchElementException("Habit not found");
                            });
                    RoutineActivity ra = new RoutineActivity();
                    ra.setHabit(habit);
                    ra.setDuration(ai.getDuration());
                    ra.setTargetTime(ai.getTargetTime() != null ? ai.getTargetTime() : 0);
                    ra.setNotes(ai.getNotes());
                    ra.setRoutine(routine);
                    acts.add(ra);
                }
                routineActivityRepository.saveAll(acts);
                routine.setActivities(acts);
            }
            routine = routineRepository.save(routine);
            RoutineOutputDTO output = io.convertToOutput(routine);
            logger.info("Updated routine {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating routine {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting routine {}", id);
        try {
            Routine routine = routineRepository.findById(id).orElse(null);
            if (routine == null) {
                logger.warn("Routine {} not found for deletion", id);
                return false;
            }
            if (routine.getActivities() != null && !routine.getActivities().isEmpty()) {
                routineActivityRepository.deleteAll(new ArrayList<>(routine.getActivities()));
            }
            routineRepository.deleteById(id);
            logger.info("Deleted routine {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting routine {}", id, ex);
            throw ex;
        }
    }

}
