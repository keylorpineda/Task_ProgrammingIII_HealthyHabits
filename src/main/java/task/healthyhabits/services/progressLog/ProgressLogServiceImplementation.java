package task.healthyhabits.services.progressLog;

import lombok.RequiredArgsConstructor;
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
import task.healthyhabits.services.progressLog.ProgressLogService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProgressLogServiceImplementation implements ProgressLogService {

    private final ProgressLogRepository progressLogRepository;
    private final CompletedActivityRepository completedActivityRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<ProgressLogDTO> list(Pageable pageable) {
        return progressLogRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(ProgressLog.class, ProgressLogDTO.class)
                        .convertToDTO(entity));
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
        InputOutputMapper<ProgressLogInputDTO, ProgressLog, ProgressLogOutputDTO> io = mapperFactory
                .createInputOutputMapper(ProgressLogInputDTO.class, ProgressLog.class, ProgressLogOutputDTO.class);
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Routine routine = routineRepository.findById(input.getRoutineId())
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        ProgressLog progressLog = new ProgressLog();
        progressLog.setUser(user);
        progressLog.setRoutine(routine);
        progressLog.setDate(input.getDate());
        progressLog = progressLogRepository.save(progressLog);
        if (input.getCompletedActivityInputs() != null) {
            List<CompletedActivity> list = new ArrayList<>();
            for (CompletedActivityInputDTO caIn : input.getCompletedActivityInputs()) {
                Habit habit = habitRepository.findById(caIn.getHabitId())
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
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
        return io.convertToOutput(progressLog);
    }

    @Override
    @Transactional
    public ProgressLogOutputDTO update(Long id, ProgressLogInputDTO input) {
        InputOutputMapper<ProgressLogInputDTO, ProgressLog, ProgressLogOutputDTO> io = mapperFactory
                .createInputOutputMapper(ProgressLogInputDTO.class, ProgressLog.class, ProgressLogOutputDTO.class);
        ProgressLog progressLog = progressLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ProgressLog not found"));
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Routine routine = routineRepository.findById(input.getRoutineId())
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
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
                        .orElseThrow(() -> new NoSuchElementException("Habit not found"));
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
        return io.convertToOutput(progressLog);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        ProgressLog progressLog = progressLogRepository.findById(id).orElse(null);
        if (progressLog == null) return false;
        if (progressLog.getCompletedActivities() != null && !progressLog.getCompletedActivities().isEmpty()) {
            completedActivityRepository.deleteAll(new ArrayList<>(progressLog.getCompletedActivities()));
        }
        progressLogRepository.deleteById(id);
        return true;
    }
}
