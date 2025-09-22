package task.healthyhabits.servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import java.util.Set;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import task.healthyhabits.services.guide.GuideServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideServiceImplementationTest {

    @Mock
    private GuideRepository guideRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<Guide, GuideDTO> guideMapper;
    @Mock
    private InputOutputMapper<GuideInputDTO, Guide, GuideOutputDTO> ioMapper;

    @InjectMocks
    private GuideServiceImplementation service;

    private Guide guide;
    private GuideDTO guideDTO;
    private GuideOutputDTO guideOutput;

    @BeforeEach
    void setUp() {
        guide = new Guide();
        guide.setId(1L);
        guide.setCategory(Category.FITNESS);
        guideDTO = new GuideDTO();
        guideDTO.setId(1L);
        guideOutput = new GuideOutputDTO();
        lenient().when(mapperFactory.createMapper(Guide.class, GuideDTO.class)).thenReturn(guideMapper);
        lenient().when(mapperFactory.createInputOutputMapper(GuideInputDTO.class, Guide.class, GuideOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @Test
    void list_delegatesToMapper() {
        Pageable pageable = PageRequest.of(0, 3);
        when(guideRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(guide), pageable, 1));
        when(guideMapper.convertToDTO(guide)).thenReturn(guideDTO);

        Page<GuideDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(guideDTO, page.getContent().getFirst());
    }

    @Test
    void recommended_filtersByCategoryAndPaginates() {
        Pageable pageable = PageRequest.of(0, 1);
        when(guideRepository.findAllByCategory(Category.FITNESS, pageable))
                .thenReturn(new PageImpl<>(List.of(guide), pageable, 1));
        when(guideMapper.convertToDTO(guide)).thenReturn(guideDTO);

        Page<GuideDTO> page = service.recommended(Category.FITNESS, null, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertSame(guideDTO, page.getContent().getFirst());
        verify(guideMapper).convertToDTO(guide);
        verify(guideRepository).findAllByCategory(Category.FITNESS, pageable);
    }

    @Test
    void recommended_filtersByFavoritesAndHandlesPagination() {
        Habit favorite = new Habit();
        favorite.setId(5L);
        User user = new User();
        user.setId(20L);
        user.setFavoriteHabits(List.of(favorite));
        guide.setRecommendedFor(List.of(favorite));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        Pageable pageable = PageRequest.of(0, 1);
        when(guideRepository.findAllRecommendedForHabits(Set.of(5L), pageable))
                .thenReturn(new PageImpl<>(List.of(guide), pageable, 1));
        GuideDTO mapped = new GuideDTO();
        mapped.setId(guide.getId());
        when(guideMapper.convertToDTO(guide)).thenReturn(mapped);

        Page<GuideDTO> page = service.recommended(null, 20L, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals(guide.getId(), page.getContent().getFirst().getId());
        verify(userRepository).findById(20L);
        verify(guideMapper).convertToDTO(guide);
        verify(guideRepository).findAllRecommendedForHabits(Set.of(5L), pageable);
    }

    @Test
    void recommended_userWithoutFavoritesReturnsEmptyPage() {
        User user = new User();
        user.setId(3L);
        user.setFavoriteHabits(new ArrayList<>());
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        Pageable pageable = PageRequest.of(0, 1);
        Page<GuideDTO> page = service.recommended(null, 3L, pageable);

        assertTrue(page.isEmpty());
        verify(guideRepository, never()).findAllRecommendedForHabits(anySet(), any());
    }

    @Test
    void recommended_userMissingThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> service.recommended(null, 99L, PageRequest.of(0, 1)));
    }

    @Test
    void create_mapsInputAndPersists() {
        GuideInputDTO input = new GuideInputDTO("title", "content", Category.FITNESS, "goal", List.of(1L));
        Habit habit = new Habit();
        habit.setId(1L);
        when(ioMapper.convertFromInput(input)).thenReturn(guide);
        when(habitRepository.findAllById(List.of(1L))).thenReturn(List.of(habit));
        when(guideRepository.save(guide)).thenReturn(guide);
        when(ioMapper.convertToOutput(guide)).thenReturn(guideOutput);

        GuideOutputDTO result = service.create(input);

        assertSame(guideOutput, result);
        assertEquals(List.of(habit), guide.getRecommendedFor());
        verify(guideRepository).save(guide);
    }

    @Test
    void update_appliesChangesAndSaves() {
        GuideInputDTO input = new GuideInputDTO("new", "body", Category.MINDFULNESS, "goal", List.of());
        guide.setTitle("old");
        guide.setContent("old");
        guide.setCategory(Category.FITNESS);
        guide.setObjective("old");
        when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));
        when(habitRepository.findAllById(List.of())).thenReturn(List.of());
        when(guideRepository.save(guide)).thenReturn(guide);
        when(ioMapper.convertToOutput(guide)).thenReturn(guideOutput);

        GuideOutputDTO result = service.update(1L, input);

        assertSame(guideOutput, result);
        assertEquals("new", guide.getTitle());
        assertEquals("body", guide.getContent());
        assertEquals(Category.MINDFULNESS, guide.getCategory());
        assertEquals("goal", guide.getObjective());
        assertNotNull(guide.getRecommendedFor());
        verify(guideRepository).save(guide);
    }

    @Test
    void delete_removesWhenExists() {
        when(guideRepository.existsById(1L)).thenReturn(true);

        assertTrue(service.delete(1L));
        verify(guideRepository).deleteById(1L);
    }

    @Test
    void delete_returnsFalseWhenMissing() {
        when(guideRepository.existsById(2L)).thenReturn(false);

        assertFalse(service.delete(2L));
    }
}