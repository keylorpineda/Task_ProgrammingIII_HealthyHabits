package task.healthyhabits.services.habit;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Habit;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HabitServiceImplementation implements HabitService {

    private static final Logger logger = LogManager.getLogger(HabitServiceImplementation.class);
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<HabitDTO> list(Pageable pageable) {
        logger.info("Listing habits with pageable {}", pageable);
        try {
            Page<HabitDTO> habits = habitRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(Habit.class, HabitDTO.class).convertToDTO(entity));
            logger.info("Listed {} habits", habits.getNumberOfElements());
            return habits;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing habits with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HabitDTO> byCategory(Category category, Pageable pageable) {
        return habitRepository.findAllByCategory(category, pageable)
                .map(entity -> mapperFactory.createMapper(Habit.class, HabitDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public HabitDTO findByIdOrNull(Long id) {
        return habitRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(Habit.class, HabitDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public HabitOutputDTO create(HabitInputDTO input) {
        logger.info("Creating habit with input {}", input);
        try {
            InputOutputMapper<HabitInputDTO, Habit, HabitOutputDTO> io =
                    mapperFactory.createInputOutputMapper(HabitInputDTO.class, Habit.class, HabitOutputDTO.class);
            Habit habit = io.convertFromInput(input);
            habit = habitRepository.save(habit);
            HabitOutputDTO output = io.convertToOutput(habit);
            logger.info("Created habit with id {}", habit.getId());
            return output;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating habit with input {}", input, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public HabitOutputDTO update(Long id, HabitInputDTO input) {
        logger.info("Updating habit {} with input {}", id, input);
        try {
            InputOutputMapper<HabitInputDTO, Habit, HabitOutputDTO> io =
                    mapperFactory.createInputOutputMapper(HabitInputDTO.class, Habit.class, HabitOutputDTO.class);
            Habit habit = habitRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Habit {} not found for update", id);
                        return new NoSuchElementException("Habit not found");
                    });
            if (input.getName() != null) habit.setName(input.getName());
            if (input.getCategory() != null) habit.setCategory(input.getCategory());
            if (input.getDescription() != null) habit.setDescription(input.getDescription());
            habit = habitRepository.save(habit);
            HabitOutputDTO output = io.convertToOutput(habit);
            logger.info("Updated habit {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating habit {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting habit {}", id);
        try {
            if (!habitRepository.existsById(id)) {
                logger.warn("Habit {} not found for deletion", id);
                return false;
            }
            habitRepository.deleteById(id);
            logger.info("Deleted habit {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting habit {}", id, ex);
            throw ex;
        }
    }
}
