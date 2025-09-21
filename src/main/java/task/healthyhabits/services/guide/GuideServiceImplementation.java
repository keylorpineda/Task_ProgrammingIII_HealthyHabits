package task.healthyhabits.services.guide;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
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
import task.healthyhabits.transformers.GenericMapper;
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

    private static final Logger logger = LogManager.getLogger(GuideServiceImplementation.class);
    private final GuideRepository guideRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<GuideDTO> list(Pageable pageable) {
        logger.info("Listing guides with pageable {}", pageable);
        try {
            Page<GuideDTO> guides = guideRepository.findAll(pageable)
                    .map(entity -> mapperFactory.createMapper(Guide.class, GuideDTO.class).convertToDTO(entity));
            logger.info("Listed {} guides", guides.getNumberOfElements());
            return guides;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing guides with pageable {}", pageable, ex);
            throw ex;
        }
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
        GenericMapper<Guide, GuideDTO> mapper = mapperFactory.createMapper(Guide.class, GuideDTO.class);
        if (category != null) {
            return guideRepository.findAllByCategory(category, pageable)
                    .map(mapper::convertToDTO);
        }
        User user = userRepository.findById(forUserId)
                .orElseThrow(() -> {
                    logger.warn("User {} not found for recommended guides", forUserId);
                    return new NoSuchElementException("User not found");
                });
        Set<Long> favIds = user.getFavoriteHabits() == null
                ? Set.of()
                : user.getFavoriteHabits().stream().map(Habit::getId).collect(Collectors.toSet());
        if (favIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return guideRepository.findAllRecommendedForHabits(favIds, pageable)
                .map(mapper::convertToDTO);
    }

    @Override
    @Transactional
    public GuideOutputDTO create(GuideInputDTO input) {
        logger.info("Creating guide with title {}", input.getTitle());
        try {
            InputOutputMapper<GuideInputDTO, Guide, GuideOutputDTO> io = mapperFactory
                    .createInputOutputMapper(GuideInputDTO.class, Guide.class, GuideOutputDTO.class);
            Guide guide = io.convertFromInput(input);
            List<Habit> rec = (input.getRecommendedHabitIds() == null || input.getRecommendedHabitIds().isEmpty())
                    ? new ArrayList<>()
                    : habitRepository.findAllById(input.getRecommendedHabitIds());
            guide.setRecommendedFor(rec);
            guide = guideRepository.save(guide);
            GuideOutputDTO output = io.convertToOutput(guide);
            logger.info("Created guide {} with title {}", guide.getId(), guide.getTitle());
            return output;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating guide with title {}", input.getTitle(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public GuideOutputDTO update(Long id, GuideInputDTO input) {
        logger.info("Updating guide {} with input {}", id, input);
        try {
            InputOutputMapper<GuideInputDTO, Guide, GuideOutputDTO> io = mapperFactory
                    .createInputOutputMapper(GuideInputDTO.class, Guide.class, GuideOutputDTO.class);
            Guide guide = guideRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Guide {} not found for update", id);
                        return new NoSuchElementException("Guide not found");
                    });
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
            GuideOutputDTO output = io.convertToOutput(guide);
            logger.info("Updated guide {} successfully", id);
            return output;
        } catch (NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating guide {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting guide {}", id);
        try {
            if (!guideRepository.existsById(id)) {
                logger.warn("Guide {} not found for deletion", id);
                return false;
            }
            guideRepository.deleteById(id);
            logger.info("Deleted guide {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting guide {}", id, ex);
            throw ex;
        }
    }
}
