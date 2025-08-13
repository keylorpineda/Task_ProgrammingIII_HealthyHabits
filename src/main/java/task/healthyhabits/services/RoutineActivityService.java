package task.healthyhabits.services;

import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.models.Routine;
import task.healthyhabits.models.Habit;

import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.HabitRepository;

import task.healthyhabits.mappers.MapperForRoutineActivity;

import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;

@Service
@Transactional
public class RoutineActivityService {

    private final RoutineActivityRepository routineActivityRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;

    public RoutineActivityService(RoutineActivityRepository routineActivityRepository,
                                  RoutineRepository routineRepository,
                                  HabitRepository habitRepository) {
        this.routineActivityRepository = routineActivityRepository;
        this.routineRepository = routineRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public Page<RoutineActivityDTO> list(Pageable pageable) {
        return routineActivityRepository.findAll(pageable).map(MapperForRoutineActivity::toDTO);
    }

    @Transactional(readOnly = true)
    public RoutineActivityDTO findByIdOrNull(Long id) {
        return routineActivityRepository.findById(id).map(MapperForRoutineActivity::toDTO).orElse(null);
    }

    public RoutineActivityOutputDTO create(Long routineId, RoutineActivityInputDTO input) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));

        RoutineActivity ra = new RoutineActivity();
        ra.setRoutine(routine);
        ra.setHabit(habit);
        ra.setDuration(input.getDuration());
        ra.setTargetTime(input.getTargetTime());
        ra.setNotes(input.getNotes());

        return MapperForRoutineActivity.toOutput(routineActivityRepository.save(ra));
    }

    public RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO input) {
        RoutineActivity ra = routineActivityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("RoutineActivity not found"));

        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));

        ra.setHabit(habit);
        ra.setDuration(input.getDuration());
        ra.setTargetTime(input.getTargetTime());
        ra.setNotes(input.getNotes());

        return MapperForRoutineActivity.toOutput(routineActivityRepository.save(ra));
    }

    public boolean delete(Long id) {
        if (!routineActivityRepository.existsById(id)) return false;
        routineActivityRepository.deleteById(id);
        return true;
    }
}
