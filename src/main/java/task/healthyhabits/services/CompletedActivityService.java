package task.healthyhabits.services;

import task.healthyhabits.models.CompletedActivity;
import task.healthyhabits.models.ProgressLog;
import task.healthyhabits.models.Habit;

import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.repositories.ProgressLogRepository;
import task.healthyhabits.repositories.HabitRepository;

import task.healthyhabits.mappers.MapperForCompletedActivity;

import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;

@Service
@Transactional
public class CompletedActivityService {

    private final CompletedActivityRepository completedActivityRepository;
    private final ProgressLogRepository progressLogRepository;
    private final HabitRepository habitRepository;

    public CompletedActivityService(CompletedActivityRepository completedActivityRepository,
                                    ProgressLogRepository progressLogRepository,
                                    HabitRepository habitRepository) {
        this.completedActivityRepository = completedActivityRepository;
        this.progressLogRepository = progressLogRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public Page<CompletedActivityDTO> list(Pageable pageable) {
        return completedActivityRepository.findAll(pageable).map(MapperForCompletedActivity::toDTO);
    }

    @Transactional(readOnly = true)
    public CompletedActivityDTO findByIdOrNull(Long id) {
        return completedActivityRepository.findById(id).map(MapperForCompletedActivity::toDTO).orElse(null);
    }

    public CompletedActivityOutputDTO create(Long progressLogId, CompletedActivityInputDTO input) {
        ProgressLog pl = progressLogRepository.findById(progressLogId)
                .orElseThrow(() -> new NoSuchElementException("ProgressLog not found"));
        Habit h = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));

        CompletedActivity ca = new CompletedActivity();
        ca.setProgressLog(pl);
        ca.setHabit(h);
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());

        return MapperForCompletedActivity.toOutput(completedActivityRepository.save(ca));
    }

    public CompletedActivityOutputDTO update(Long id, CompletedActivityInputDTO input) {
        CompletedActivity ca = completedActivityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("CompletedActivity not found"));

        Habit h = habitRepository.findById(input.getHabitId())
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));

        ca.setHabit(h);
        ca.setCompletedAt(input.getCompletedAt());
        ca.setNotes(input.getNotes());

        return MapperForCompletedActivity.toOutput(completedActivityRepository.save(ca));
    }

    public boolean delete(Long id) {
        if (!completedActivityRepository.existsById(id)) return false;
        completedActivityRepository.deleteById(id);
        return true;
    }
}
