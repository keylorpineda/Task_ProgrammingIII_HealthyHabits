package task.healthyhabits.resolvers.guides;

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

import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.guides.GuideMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.guide.GuideService;

@ExtendWith(MockitoExtension.class)
class GuideMutationResolverTest {

    @Mock
    private GuideService guideService;

    @InjectMocks
    private GuideMutationResolver resolver;

    @Test
    void createGuideDelegatesToService() {
        GuideInputDTO input = new GuideInputDTO("Morning Guide", "Content", Category.PHYSICAL, "Energy", List.of(1L, 2L));
        GuideOutputDTO output = new GuideOutputDTO(10L, "Morning Guide", "Content", Category.PHYSICAL, "Energy", List.of());
        when(guideService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuideOutputDTO result = resolver.createGuide(input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.GUIDE_EDITOR));
        }

        verify(guideService).create(input);
    }

    @Test
    void updateGuideDelegatesToService() {
        GuideInputDTO input = new GuideInputDTO("Evening Guide", "Content", Category.MENTAL, "Relax", List.of());
        GuideOutputDTO output = new GuideOutputDTO(11L, "Evening Guide", "Content", Category.MENTAL, "Relax", List.of(new HabitOutputDTO()));
        when(guideService.update(11L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            GuideOutputDTO result = resolver.updateGuide(11L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.GUIDE_EDITOR));
        }

        verify(guideService).update(11L, input);
    }

    @Test
    void deleteGuideDelegatesToService() {
        when(guideService.delete(12L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteGuide(12L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.GUIDE_EDITOR));
        }

        verify(guideService).delete(12L);
    }
}
