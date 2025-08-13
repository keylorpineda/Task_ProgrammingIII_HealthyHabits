package task.healthyhabits.services;

import task.healthyhabits.models.Routine;
import task.healthyhabits.models.RoutineActivity;
import task.healthyhabits.models.User;
import task.healthyhabits.models.Habit;

import task.healthyhabits.repositories.RoutineRepository;
import task.healthyhabits.repositories.RoutineActivityRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.repositories.HabitRepository;

import task.healthyhabits.mappers.MapperForRoutine;

import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineActivityRepository routineActivityRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public RoutineService(RoutineRepository routineRepository,
                          RoutineActivityRepository routineActivityRepository,
                          UserRepository userRepository,
                          HabitRepository habitRepository) {
        this.routineRepository = routineRepository;
        this.routineActivityRepository = routineActivityRepository;
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public Page<RoutineDTO> list(Pageable pageable) {
        return routineRepository.findAll(pageable).map(MapperForRoutine::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<RoutineDTO> myRoutines(Long userId, Pageable pageable) {
        List<RoutineDTO> filtered = routineRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId() != null && r.getUser().getId().equals(userId))
                .map(MapperForRoutine::toDTO)
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RoutineDTO> byUser(Long userId, Pageable pageable) {
        return myRoutines(userId, pageable);
    }

    @Transactional(readOnly = true)
    public RoutineDTO findByIdOrNull(Long id) {
        return routineRepository.findById(id).map(MapperForRoutine::toDTO).orElse(null);
    }

    public RoutineOutputDTO create(RoutineInputDTO input) {
        User u = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Routine r = new Routine();
        r.setTitle(input.getTitle());
        r.setDescription(input.getDescription());
        r.setDaysOfWeek(input.getDaysOfWeek());
        r.setUser(u);
        r.setActivities(new ArrayList<>());
        r = routineRepository.save(r);

        if (input.getActivityInputs() != null) {
            List<RoutineActivity> acts = new ArrayList<>();
            for (RoutineActivityInputDTO ai : input.getActivityInputs()) {
                Habit h = habitRepository.findById(ai.getHabitId())
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
                RoutineActivity ra = new RoutineActivity();
                ra.setHabit(h);
                ra.setDuration(ai.getDuration());
                ra.setTargetTime(ai.getTargetTime());
                ra.setNotes(ai.getNotes());
                ra.setRoutine(r);
                acts.add(ra);
            }
            routineActivityRepository.saveAll(acts);
            r.setActivities(acts);
        }
        return MapperForRoutine.toOutput(r);
    }

    public RoutineOutputDTO update(Long id, RoutineInputDTO input) {
        Routine r = routineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));

        User u = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        r.setTitle(input.getTitle());
        r.setDescription(input.getDescription());
        r.setDaysOfWeek(input.getDaysOfWeek());
        r.setUser(u);

        List<RoutineActivity> old = new ArrayList<>(r.getActivities() == null ? List.of() : r.getActivities());
        if (!old.isEmpty()) {
            routineActivityRepository.deleteAll(old);
            r.getActivities().clear();
        }

        if (input.getActivityInputs() != null) {
            List<RoutineActivity> acts = new ArrayList<>();
            for (RoutineActivityInputDTO ai : input.getActivityInputs()) {
                Habit h = habitRepository.findById(ai.getHabitId())
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
                RoutineActivity ra = new RoutineActivity();
                ra.setHabit(h);
                ra.setDuration(ai.getDuration());
                ra.setTargetTime(ai.getTargetTime());
                ra.setNotes(ai.getNotes());
                ra.setRoutine(r);
                acts.add(ra);
            }
            routineActivityRepository.saveAll(acts);
            r.setActivities(acts);
        }

        r = routineRepository.save(r);
        return MapperForRoutine.toOutput(r);
    }

    public boolean delete(Long id) {
        Routine r = routineRepository.findById(id).orElse(null);
        if (r == null) return false;
        if (r.getActivities() != null && !r.getActivities().isEmpty()) {
            routineActivityRepository.deleteAll(new ArrayList<>(r.getActivities()));
        }
        routineRepository.deleteById(id);
        return true;
    }

    private static <T> Page<T> paginate(List<T> all, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        if (offset >= all.size()) return new PageImpl<>(List.of(), pageable, all.size());
        List<T> content = all.stream().skip(offset).limit(size).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, all.size());
    }
}
