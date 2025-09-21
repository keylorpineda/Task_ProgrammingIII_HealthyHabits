package task.healthyhabits.services.routineActivity;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoutineActivityServiceImplementation implements RoutineActivityService {

    private static final Logger logger = LogManager.getLogger(RoutineActivityServiceImplementation.class);
    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> list(Pageable pageable) {
        logger.info("Listing routine activities with pageable {}", pageable);
        try {
            Page<RoutineActivityDTO> activities = routineActivityRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(RoutineActivity.class, RoutineActivityDTO.class).convertToDTO(entity));
            logger.info("Listed {} routine activities", activities.getNumberOfElements());
            return activities;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing routine activities with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineActivityDTO findByIdOrNull(Long id) {
        return routineActivityRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(RoutineActivity.class, RoutineActivityDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public RoutineActivityOutputDTO create(Long routineId, RoutineActivityInputDTO input) {
        logger.info("Creating routine activity for routine {} with input {}", routineId, input);
        try {
            InputOutputMapper<RoutineActivityInputDTO, RoutineActivity, RoutineActivityOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoutineActivityInputDTO.class, RoutineActivity.class, RoutineActivityOutputDTO.class);
            Routine routine = routineRepository.findById(routineId)
                    .orElseThrow(() -> {
                        logger.warn("Routine {} not found for activity creation", routineId);
                        return new NoSuchElementException("Routine not found");
                    });
            Habit habit = habitRepository.findById(input.getHabitId())
                    .orElseThrow(() -> {
                        logger.warn("Habit {} not found for routine activity creation", input.getHabitId());
                        return new NoSuchElementException("Habit not found");
                    });
            RoutineActivity ra = new RoutineActivity();
            ra.setRoutine(routine);
            ra.setHabit(habit);
            ra.setDuration(input.getDuration());
            ra.setTargetTime(input.getTargetTime() != null ? input.getTargetTime() : 0);
            ra.setNotes(input.getNotes());
            ra = routineActivityRepository.save(ra);
            RoutineActivityOutputDTO output = io.convertToOutput(ra);
            logger.info("Created routine activity {} for routine {}", ra.getId(), routineId);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating routine activity for routine {}", routineId, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO input) {
        logger.info("Updating routine activity {} with input {}", id, input);
        try {
            InputOutputMapper<RoutineActivityInputDTO, RoutineActivity, RoutineActivityOutputDTO> io =
                    mapperFactory.createInputOutputMapper(RoutineActivityInputDTO.class, RoutineActivity.class, RoutineActivityOutputDTO.class);
            RoutineActivity ra = routineActivityRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Routine activity {} not found for update", id);
                        return new NoSuchElementException("RoutineActivity not found");
                    });
            Habit habit = habitRepository.findById(input.getHabitId())
                    .orElseThrow(() -> {
                        logger.warn("Habit {} not found for routine activity update", input.getHabitId());
                        return new NoSuchElementException("Habit not found");
                    });
            ra.setHabit(habit);
            if (input.getDuration() != null) ra.setDuration(input.getDuration());
            if (input.getTargetTime() != null) ra.setTargetTime(input.getTargetTime());
            ra.setNotes(input.getNotes());
            ra = routineActivityRepository.save(ra);
            RoutineActivityOutputDTO output = io.convertToOutput(ra);
            logger.info("Updated routine activity {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating routine activity {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting routine activity {}", id);
        try {
            if (!routineActivityRepository.existsById(id)) {
                logger.warn("Routine activity {} not found for deletion", id);
                return false;
            }
            routineActivityRepository.deleteById(id);
            logger.info("Deleted routine activity {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting routine activity {}", id, ex);
            throw ex;
        }
    }
}
