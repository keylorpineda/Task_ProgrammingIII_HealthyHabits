package task.healthyhabits.tests.resolvers.routineActivities;

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

import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.routineActivities.RoutineActivityQueryResolver;
import task.healthyhabits.resolvers.routineActivities.RoutineActivityQueryResolver.RoutineActivityPage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.routineActivity.RoutineActivityService;

@ExtendWith(MockitoExtension.class)
class RoutineActivityQueryResolverTest {

    @Mock
    private RoutineActivityService routineActivityService;

    @InjectMocks
    private RoutineActivityQueryResolver resolver;

    @Test
    void listRoutineActivitiesReturnsServicePage() {
        HabitDTO habit = new HabitDTO(2L, "Jog", Category.PHYSICAL, "Morning jog");
        RoutineActivityDTO dto = new RoutineActivityDTO(1L, habit, 30, 45, "", null);
        Pageable pageable = PageRequest.of(0, 5);
        Page<RoutineActivityDTO> page = new PageImpl<>(List.of(dto), pageable, 1);
        when(routineActivityService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineActivityPage result = resolver.listRoutineActivities(0, 5);

            assertThat(result.content()).containsExactly(dto);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(routineActivityService).list(pageable);
    }

    @Test
    void getRoutineActivityByIdDelegatesToService() {
        HabitDTO habit = new HabitDTO(3L, "Stretch", Category.PHYSICAL, "Evening stretch");
        RoutineActivityDTO dto = new RoutineActivityDTO(5L, habit, 15, 20, "", null);
        when(routineActivityService.findByIdOrNull(5L)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineActivityDTO result = resolver.getRoutineActivityById(5L);

            assertThat(result).isEqualTo(dto);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(routineActivityService).findByIdOrNull(5L);
    }
}
