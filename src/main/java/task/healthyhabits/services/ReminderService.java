package task.healthyhabits.services;

import task.healthyhabits.models.Reminder;
import task.healthyhabits.repositories.ReminderRepository;
import task.healthyhabits.mappers.MapperForReminder;
import task.healthyhabits.mappers.MapperForUser;
import task.healthyhabits.mappers.MapperForHabit;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReminderDTO> list(Pageable pageable) {
        return reminderRepository.findAll(pageable).map(MapperForReminder::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReminderDTO> myReminders(Long userId, Pageable pageable) {
        List<ReminderDTO> filtered = reminderRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId() != null && r.getUser().getId().equals(userId))
                .map(MapperForReminder::toDTO)
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    @Transactional(readOnly = true)
    public ReminderDTO findByIdOrNull(Long id) {
        return reminderRepository.findById(id).map(MapperForReminder::toDTO).orElse(null);
    }

    public ReminderOutputDTO create(ReminderInputDTO input) {
        Reminder r = new Reminder();
        r.setUser(MapperForUser.toModel(input.getUser()));
        r.setHabit(MapperForHabit.toModel(input.getHabit()));
        r.setTime(input.getTime());
        r.setFrequency(input.getFrequency());
        return MapperForReminder.toOutput(reminderRepository.save(r));
    }

    public ReminderOutputDTO update(Long id, ReminderInputDTO input) {
        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found"));
        r.setUser(MapperForUser.toModel(input.getUser()));
        r.setHabit(MapperForHabit.toModel(input.getHabit()));
        r.setTime(input.getTime());
        r.setFrequency(input.getFrequency());
        return MapperForReminder.toOutput(reminderRepository.save(r));
    }

    public boolean delete(Long id) {
        if (!reminderRepository.existsById(id)) return false;
        reminderRepository.deleteById(id);
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
