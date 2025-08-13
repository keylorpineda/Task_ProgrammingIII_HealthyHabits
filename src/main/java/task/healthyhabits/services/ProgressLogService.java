package task.healthyhabits.services;

import task.healthyhabits.models.*;
import task.healthyhabits.repositories.*;
import task.healthyhabits.mappers.MapperForProgressLog;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgressLogService {

    private final ProgressLogRepository progressLogRepository;
    private final CompletedActivityRepository completedActivityRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;

    public ProgressLogService(ProgressLogRepository progressLogRepository,
                              CompletedActivityRepository completedActivityRepository,
                              UserRepository userRepository,
                              RoutineRepository routineRepository,
                              HabitRepository habitRepository) {
        this.progressLogRepository = progressLogRepository;
        this.completedActivityRepository = completedActivityRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> list(Pageable pageable) {
        return progressLogRepository.findAll(pageable).map(MapperForProgressLog::toDTO);
    }

    @Transactional(readOnly = true)
    public ProgressLogDTO findByIdOrNull(Long id) {
        return progressLogRepository.findById(id).map(MapperForProgressLog::toDTO).orElse(null);
    }

    @Transactional(readOnly = true)
    public ProgressLogDTO byDate(Long userId, LocalDate date) {
        return progressLogRepository.findAll().stream()
                .filter(pl -> pl.getUser() != null && pl.getUser().getId() != null && pl.getUser().getId().equals(userId))
                .filter(pl -> date.equals(pl.getDate()))
                .findFirst()
                .map(MapperForProgressLog::toDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> byRange(Long userId, LocalDate from, LocalDate to, Pageable pageable) {
        List<ProgressLogDTO> filtered = progressLogRepository.findAll().stream()
                .filter(pl -> pl.getUser() != null && pl.getUser().getId() != null && pl.getUser().getId().equals(userId))
                .filter(pl -> !pl.getDate().isBefore(from) && !pl.getDate().isAfter(to))
                .map(MapperForProgressLog::toDTO)
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    public ProgressLogOutputDTO create(ProgressLogInputDTO input) {
        User u = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Routine r = routineRepository.findById(input.getRoutineId())
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));

        ProgressLog pl = new ProgressLog();
        pl.setUser(u);
        pl.setRoutine(r);
        pl.setDate(input.getDate());
        pl = progressLogRepository.save(pl);

        if (input.getCompletedActivityInputs() != null) {
            List<CompletedActivity> list = new ArrayList<>();
            for (CompletedActivityInputDTO caIn : input.getCompletedActivityInputs()) {
                Habit h = habitRepository.findById(caIn.getHabitId())
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
                CompletedActivity ca = new CompletedActivity();
                ca.setHabit(h);
                ca.setCompletedAt(caIn.getCompletedAt());
                ca.setNotes(caIn.getNotes());
                ca.setProgressLog(pl);
                list.add(ca);
            }
            completedActivityRepository.saveAll(list);
            pl.setCompletedActivities(list);
        }
        return MapperForProgressLog.toOutput(pl);
    }

    public ProgressLogOutputDTO update(Long id, ProgressLogInputDTO input) {
        ProgressLog pl = progressLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ProgressLog not found"));

        User u = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Routine r = routineRepository.findById(input.getRoutineId())
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));

        pl.setUser(u);
        pl.setRoutine(r);
        pl.setDate(input.getDate());

        if (pl.getCompletedActivities() != null && !pl.getCompletedActivities().isEmpty()) {
            completedActivityRepository.deleteAll(new ArrayList<>(pl.getCompletedActivities()));
            pl.getCompletedActivities().clear();
        }

        if (input.getCompletedActivityInputs() != null) {
            List<CompletedActivity> list = new ArrayList<>();
            for (CompletedActivityInputDTO caIn : input.getCompletedActivityInputs()) {
                Habit h = habitRepository.findById(caIn.getHabitId())
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
                CompletedActivity ca = new CompletedActivity();
                ca.setHabit(h);
                ca.setCompletedAt(caIn.getCompletedAt());
                ca.setNotes(caIn.getNotes());
                ca.setProgressLog(pl);
                list.add(ca);
            }
            completedActivityRepository.saveAll(list);
            pl.setCompletedActivities(list);
        }

        pl = progressLogRepository.save(pl);
        return MapperForProgressLog.toOutput(pl);
    }

    public boolean delete(Long id) {
        ProgressLog pl = progressLogRepository.findById(id).orElse(null);
        if (pl == null) return false;
        if (pl.getCompletedActivities() != null && !pl.getCompletedActivities().isEmpty()) {
            completedActivityRepository.deleteAll(new ArrayList<>(pl.getCompletedActivities()));
        }
        progressLogRepository.deleteById(id);
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
