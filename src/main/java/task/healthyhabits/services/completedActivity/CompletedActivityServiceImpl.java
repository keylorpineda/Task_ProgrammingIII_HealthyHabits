package task.healthyhabits.services.completedActivity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.ProgressLogRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CompletedActivityServiceImpl implements CompletedActivityService {

    private final CompletedActivityRepository completedActivityRepository;
    private final ProgressLogRepository progressLogRepository;
    private final HabitRepository habitRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<CompletedActivityDTO> list(Pageable pageable) {
        return completedActivityRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(CompletedActivity.class, CompletedActivityDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CompletedActivityDTO findByIdOrNull(Long id) {
        return completedActivityRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(CompletedActivity.class, CompletedActivityDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional
    public CompletedActivityOutputDTO create(Long progressLogId, CompletedActivityInputDTO input) {
        InputOutputMapper<CompletedActivityInputDTO, CompletedActivity, CompletedActivityOutputDTO> io =
                mapperFactory.createInputOutputMapper(CompletedActivityInputDTO.class, CompletedActivity.class, CompletedActivityOutputDTO.class);
        ProgressLog progressLog = progressLogRepository.findById(progressLogId)
                .orElseThrow(() -> new NoSuchElementException("ProgressLog not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        CompletedActivity ca = new CompletedActivity();
        ca.setProgressLog(progressLog);
        ca.setHabit(habit);
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());
        ca = completedActivityRepository.save(ca);
        return io.convertToOutput(ca);
    }

    @Override
    @Transactional
    public CompletedActivityOutputDTO update(Long id, CompletedActivityInputDTO input) {
        InputOutputMapper<CompletedActivityInputDTO, CompletedActivity, CompletedActivityOutputDTO> io =
                mapperFactory.createInputOutputMapper(CompletedActivityInputDTO.class, CompletedActivity.class, CompletedActivityOutputDTO.class);
        CompletedActivity ca = completedActivityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("CompletedActivity not found"));
        Habit habit = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        ca.setHabit(habit);
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());
        ca = completedActivityRepository.save(ca);
        return io.convertToOutput(ca);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!completedActivityRepository.existsById(id)) return false;
        completedActivityRepository.deleteById(id);
        return true;
    }
}
