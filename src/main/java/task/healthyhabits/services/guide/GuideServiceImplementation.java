package task.healthyhabits.services.guide;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.dtos.normals.GuideDTO;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Guide;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.GuideRepository;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImplementation implements GuideService {

    private final GuideRepository guideRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<GuideDTO> list(Pageable pageable) {
        return guideRepository.findAll(pageable)
                .map(entity -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuideDTO> listByObjective(String objective, Pageable pageable) {
        return guideRepository.findAllByObjective(objective, pageable)
                .map(entity -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GuideDTO findByIdOrNull(Long id) {
        return guideRepository.findById(id)
                .map(entity -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(entity))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuideDTO> recommended(Category category, Long forUserId, Pageable pageable) {
        if (category != null) {
            List<GuideDTO> filtered = guideRepository.findAll().stream()
                    .filter(g -> g.getCategory() == category)
                    .map(g -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(g))
                    .toList();
            return paginate(filtered, pageable);
        }
        User user = userRepository.findById(forUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Set<Long> favIds = user.getFavoriteHabits() == null
                ? Set.of()
                : user.getFavoriteHabits().stream().map(Habit::getId).collect(Collectors.toSet());
        if (favIds.isEmpty()) {
            return Page.empty(pageable);
        }
        List<GuideDTO> filtered = guideRepository.findAll().stream()
                .filter(g -> g.getRecommendedFor() != null &&
                        g.getRecommendedFor().stream().anyMatch(h -> favIds.contains(h.getId())))
                .map(g -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(g))
                .toList();
        return paginate(filtered, pageable);
    }

    @Override
    @Transactional
    public GuideOutputDTO create(GuideInputDTO input) {
        InputOutputMapper<GuideInputDTO, Guide, GuideOutputDTO> io = mapperFactory
                .createInputOutputMapper(GuideInputDTO.class, Guide.class, GuideOutputDTO.class);
        Guide guide = io.convertFromInput(input);
        List<Habit> rec = (input.getRecommendedHabitIds() == null || input.getRecommendedHabitIds().isEmpty())
                ? new ArrayList<>()
                : habitRepository.findAllById(input.getRecommendedHabitIds());
        guide.setRecommendedFor(rec);
        guide = guideRepository.save(guide);
        return io.convertToOutput(guide);
    }

    @Override
    @Transactional
    public GuideOutputDTO update(Long id, GuideInputDTO input) {
        InputOutputMapper<GuideInputDTO, Guide, GuideOutputDTO> io = mapperFactory
                .createInputOutputMapper(GuideInputDTO.class, Guide.class, GuideOutputDTO.class);
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Guide not found"));
        if (input.getTitle() != null)
            guide.setTitle(input.getTitle());
        if (input.getContent() != null)
            guide.setContent(input.getContent());
        if (input.getCategory() != null)
            guide.setCategory(input.getCategory());
        if (input.getObjective() != null)
            guide.setObjective(input.getObjective());
        if (input.getRecommendedHabitIds() != null) {
            List<Long> ids = input.getRecommendedHabitIds();
            List<Habit> rec = ids.isEmpty() ? new ArrayList<>() : habitRepository.findAllById(ids);
            guide.setRecommendedFor(rec);
        }
        guide = guideRepository.save(guide);
        return io.convertToOutput(guide);
    }

    private static <T> Page<T> paginate(List<T> all, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        if (offset >= all.size())
            return new PageImpl<>(new ArrayList<>(), pageable, all.size());
        List<T> content = all.stream().skip(offset).limit(size).toList();
        return new PageImpl<>(content, pageable, all.size());
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!guideRepository.existsById(id))
            return false;
        guideRepository.deleteById(id);
        return true;
    }
}
