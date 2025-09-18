package task.healthyhabits.services.routineActivity;

import lombok.RequiredArgsConstructor;
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
import task.healthyhabits.services.routineActivity.RoutineActivityService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoutineActivityServiceImplementation implements RoutineActivityService {

    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> list(Pageable pageable) {
        return routineActivityRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(RoutineActivity.class, RoutineActivityDTO.class).convertToDTO(entity));
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
        InputOutputMapper<RoutineActivityInputDTO, RoutineActivity, RoutineActivityOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoutineActivityInputDTO.class, RoutineActivity.class, RoutineActivityOutputDTO.class);
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        RoutineActivity ra = new RoutineActivity();
        ra.setRoutine(routine);
        ra.setHabit(habit);
        ra.setDuration(input.getDuration());
        ra.setTargetTime(input.getTargetTime() != null ? input.getTargetTime() : 0);
        ra.setNotes(input.getNotes());
        ra = routineActivityRepository.save(ra);
        return io.convertToOutput(ra);
    }

    @Override
    @Transactional
    public RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO input) {
        InputOutputMapper<RoutineActivityInputDTO, RoutineActivity, RoutineActivityOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoutineActivityInputDTO.class, RoutineActivity.class, RoutineActivityOutputDTO.class);
        RoutineActivity ra = routineActivityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("RoutineActivity not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        ra.setHabit(habit);
        if (input.getDuration() != null) ra.setDuration(input.getDuration());
        if (input.getTargetTime() != null) ra.setTargetTime(input.getTargetTime());
        ra.setNotes(input.getNotes());
        ra = routineActivityRepository.save(ra);
        return io.convertToOutput(ra);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!routineActivityRepository.existsById(id)) return false;
        routineActivityRepository.deleteById(id);
        return true;
    }
}
