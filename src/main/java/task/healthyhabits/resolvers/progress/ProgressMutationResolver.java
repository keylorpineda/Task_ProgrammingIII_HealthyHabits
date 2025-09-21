package task.healthyhabits.resolvers.progress;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.completedActivity.CompletedActivityService;
import task.healthyhabits.services.progressLog.ProgressLogService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class ProgressMutationResolver {

    private final ProgressLogService progressLogService;
    private final CompletedActivityService completedActivityService;

    @MutationMapping
    public ProgressLogOutputDTO createProgressLog(@Argument("input") @Valid ProgressLogInputDTO input) {
        requireAny(Permission.PROGRESS_EDITOR);
        return progressLogService.create(input);
    }

    @MutationMapping
    public ProgressLogOutputDTO updateProgressLog(@Argument Long id, @Argument("input") @Valid ProgressLogInputDTO input) {
        requireAny(Permission.PROGRESS_EDITOR);
        return progressLogService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteProgressLog(@Argument Long id) {
        requireAny(Permission.PROGRESS_EDITOR);
        return progressLogService.delete(id);
    }

    @MutationMapping
    public CompletedActivityOutputDTO createCompletedActivity(@Argument Long progressLogId,
                                                              @Argument("input") @Valid CompletedActivityInputDTO input) {
        requireAny(Permission.PROGRESS_EDITOR);
        return completedActivityService.create(progressLogId, input);
    }

    @MutationMapping
    public CompletedActivityOutputDTO updateCompletedActivity(@Argument Long id,
                                                              @Argument("input") @Valid CompletedActivityInputDTO input) {
        requireAny(Permission.PROGRESS_EDITOR);
        return completedActivityService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteCompletedActivity(@Argument Long id) {
        requireAny(Permission.PROGRESS_EDITOR);
        return completedActivityService.delete(id);
    }
}
