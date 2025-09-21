package task.healthyhabits.tests.resolvers.guides;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import task.healthyhabits.dtos.normals.GuideDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.resolvers.guides.GuideQueryResolver;
import task.healthyhabits.resolvers.guides.GuideQueryResolver.GuidePage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.guide.GuideService;

@ExtendWith(MockitoExtension.class)
class GuideQueryResolverTest {

    @Mock
    private GuideService guideService;

    @InjectMocks
    private GuideQueryResolver resolver;

    @Test
    void listGuidesReturnsServicePage() {
        Pageable pageable = PageRequest.of(0, 6);
        GuideDTO guide = new GuideDTO(1L, "Morning Routine", "Content", Category.PHYSICAL, "Start strong", List.of());
        Page<GuideDTO> page = new PageImpl<>(List.of(guide), pageable, 1);
        when(guideService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuidePage result = resolver.listGuides(0, 6);

            assertThat(result.content()).containsExactly(guide);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(guideService).list(pageable);
    }

    @Test
    void listGuidesByObjectiveFiltersByObjective() {
        Pageable pageable = PageRequest.of(1, 2);
        GuideDTO guide = new GuideDTO(2L, "Sleep Better", "Content", Category.SLEEP, "Rest", List.of());
        Page<GuideDTO> page = new PageImpl<>(List.of(guide), pageable, 1);
        when(guideService.listByObjective("Rest", pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuidePage result = resolver.listGuidesByObjective("Rest", 1, 2);

            assertThat(result.content()).containsExactly(guide);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
        }

        verify(guideService).listByObjective("Rest", pageable);
    }

    @Test
    void listRecommendedGuidesUsesServiceRecommendation() {
        Pageable pageable = PageRequest.of(0, 3);
        HabitDTO habit = new HabitDTO(3L, "Walk", Category.PHYSICAL, "30-minute walk");
        GuideDTO guide = new GuideDTO(5L, "Wellness", "Content", Category.PHYSICAL, "Improve health", List.of(habit));
        Page<GuideDTO> page = new PageImpl<>(List.of(guide), pageable, 1);
        when(guideService.recommended(Category.PHYSICAL, 7L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuidePage result = resolver.listRecommendedGuides(Category.PHYSICAL, 7L, 0, 3);

            assertThat(result.content()).containsExactly(guide);
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
        }

        verify(guideService).recommended(Category.PHYSICAL, 7L, pageable);
    }

    @Test
    void getGuideByIdDelegatesToService() {
        GuideDTO guide = new GuideDTO(9L, "Focus", "Content", Category.MENTAL, "Concentrate", List.of());
        when(guideService.findByIdOrNull(9L)).thenReturn(guide);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuideDTO result = resolver.getGuideById(9L);
            assertThat(result).isEqualTo(guide);
        }

        verify(guideService).findByIdOrNull(9L);
    }
}
