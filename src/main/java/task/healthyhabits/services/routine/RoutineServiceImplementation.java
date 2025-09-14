package task.healthyhabits.services.routine;

import lombok.RequiredArgsConstructor;
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
import task.healthyhabits.services.routine.RoutineService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoutineServiceImplementation implements RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineActivityRepository routineActivityRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineDTO> list(Pageable pageable) {
        return routineRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(Routine.class, RoutineDTO.class).convertToDTO(entity));
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
        InputOutputMapper<RoutineInputDTO, Routine, RoutineOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoutineInputDTO.class, Routine.class, RoutineOutputDTO.class);
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
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
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
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
        return io.convertToOutput(routine);
    }

    @Override
    @Transactional
    public RoutineOutputDTO update(Long id, RoutineInputDTO input) {
        InputOutputMapper<RoutineInputDTO, Routine, RoutineOutputDTO> io =
                mapperFactory.createInputOutputMapper(RoutineInputDTO.class, Routine.class, RoutineOutputDTO.class);
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
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
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
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
        return io.convertToOutput(routine);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Routine routine = routineRepository.findById(id).orElse(null);
        if (routine == null) return false;
        if (routine.getActivities() != null && !routine.getActivities().isEmpty()) {
            routineActivityRepository.deleteAll(new ArrayList<>(routine.getActivities()));
        }
        routineRepository.deleteById(id);
        return true;
    }

}
