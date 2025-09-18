package task.healthyhabits.services.habit;

import lombok.RequiredArgsConstructor;
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
import task.healthyhabits.services.habit.HabitService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HabitServiceImplementation implements HabitService {

    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<HabitDTO> list(Pageable pageable) {
        return habitRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(Habit.class, HabitDTO.class).convertToDTO(entity));
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
        InputOutputMapper<HabitInputDTO, Habit, HabitOutputDTO> io =
                mapperFactory.createInputOutputMapper(HabitInputDTO.class, Habit.class, HabitOutputDTO.class);
        Habit habit = io.convertFromInput(input);
        habit = habitRepository.save(habit);
        return io.convertToOutput(habit);
    }

    @Override
    @Transactional
    public HabitOutputDTO update(Long id, HabitInputDTO input) {
        InputOutputMapper<HabitInputDTO, Habit, HabitOutputDTO> io =
                mapperFactory.createInputOutputMapper(HabitInputDTO.class, Habit.class, HabitOutputDTO.class);
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        if (input.getName() != null) habit.setName(input.getName());
        if (input.getCategory() != null) habit.setCategory(input.getCategory());
        if (input.getDescription() != null) habit.setDescription(input.getDescription());
        habit = habitRepository.save(habit);
        return io.convertToOutput(habit);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!habitRepository.existsById(id)) return false;
        habitRepository.deleteById(id);
        return true;
    }
}
