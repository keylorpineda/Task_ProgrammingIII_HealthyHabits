package task.healthyhabits.services;

import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Category;

import task.healthyhabits.repositories.HabitRepository;

import task.healthyhabits.mappers.MapperForHabit;

import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;

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
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public Page<HabitDTO> list(Pageable pageable) {
        return habitRepository.findAll(pageable).map(MapperForHabit::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HabitDTO> byCategory(Category category, Pageable pageable) {
        List<HabitDTO> filtered = habitRepository.findAll().stream()
                .filter(h -> h.getCategory() == category)
                .map(MapperForHabit::toDTO)
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    @Transactional(readOnly = true)
    public HabitDTO findByIdOrNull(Long id) {
        return habitRepository.findById(id).map(MapperForHabit::toDTO).orElse(null);
    }

    public HabitOutputDTO create(HabitInputDTO input) {
        Habit h = MapperForHabit.toModel(input);
        return MapperForHabit.toOutput(habitRepository.save(h));
    }

    public HabitOutputDTO update(Long id, HabitInputDTO input) {
        Habit h = habitRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Habit not found"));
        h.setName(input.getName());
        h.setCategory(input.getCategory());
        h.setDescription(input.getDescription());
        return MapperForHabit.toOutput(habitRepository.save(h));
    }

    public boolean delete(Long id) {
        if (!habitRepository.existsById(id)) return false;
        habitRepository.deleteById(id);
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
