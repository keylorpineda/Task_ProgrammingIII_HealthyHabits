package task.healthyhabits.services;

import task.healthyhabits.models.Guide;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.User;

import task.healthyhabits.repositories.GuideRepository;
import task.healthyhabits.repositories.UserRepository;

import task.healthyhabits.mappers.MapperForGuide;
import task.healthyhabits.mappers.MapperForHabit;

import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.dtos.normals.GuideDTO;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;

    public GuideService(GuideRepository guideRepository,
            UserRepository userRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<GuideDTO> list(Pageable pageable) {
        return guideRepository.findAll(pageable).map(MapperForGuide::toDTO);
    }

    @Transactional(readOnly = true)
    public GuideDTO findByIdOrNull(Long id) {
        return guideRepository.findById(id).map(MapperForGuide::toDTO).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<GuideDTO> recommended(Category category, Long forUserId, Pageable pageable) {
        if (category != null) {
            List<GuideDTO> filtered = guideRepository.findAll().stream()
                    .filter(g -> g.getCategory() == category)
                    .map(MapperForGuide::toDTO)
                    .collect(Collectors.toList());
            return paginate(filtered, pageable);
        }
        User u = userRepository.findById(forUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Set<Long> favIds = u.getFavoriteHabits().stream().map(Habit::getId).collect(Collectors.toSet());
        if (favIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        List<GuideDTO> filtered = guideRepository.findAll().stream()
                .filter(g -> g.getRecommendedFor() != null
                        && g.getRecommendedFor().stream().anyMatch(h -> favIds.contains(h.getId())))
                .map(MapperForGuide::toDTO)
                .collect(Collectors.toList());
        return paginate(filtered, pageable);
    }

    public GuideOutputDTO create(GuideInputDTO input) {
        Guide g = new Guide();
        g.setTitle(input.getTitle());
        g.setContent(input.getContent());
        g.setCategory(input.getCategory());
        List<Habit> rec = (input.getRecommendedFor() == null)
                ? new ArrayList<>()
                : input.getRecommendedFor().stream().map(MapperForHabit::toModel).collect(Collectors.toList());
        g.setRecommendedFor(rec);
        return MapperForGuide.toOutput(guideRepository.save(g));
    }

    public GuideOutputDTO update(Long id, GuideInputDTO input) {
        Guide g = guideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Guide not found"));
        g.setTitle(input.getTitle());
        g.setContent(input.getContent());
        g.setCategory(input.getCategory());
        List<Habit> rec = (input.getRecommendedFor() == null)
                ? new ArrayList<>()
                : input.getRecommendedFor().stream().map(MapperForHabit::toModel).collect(Collectors.toList());
        g.setRecommendedFor(rec);
        return MapperForGuide.toOutput(guideRepository.save(g));
    }

    public boolean delete(Long id) {
        if (!guideRepository.existsById(id))
            return false;
        guideRepository.deleteById(id);
        return true;
    }

    private static <T> Page<T> paginate(List<T> all, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        if (offset >= all.size())
            return new PageImpl<>(List.of(), pageable, all.size());
        List<T> content = all.stream().skip(offset).limit(size).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, all.size());
    }
}
