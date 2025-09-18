package task.healthyhabits.resolvers.guides;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.inputs.GuideInputDTO;
import jakarta.validation.Valid;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.guide.GuideService;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class GuideMutationResolver {

    private final GuideService guideService;

    @MutationMapping
    public GuideOutputDTO createGuide(@Argument("input") @Valid GuideInputDTO input) {
        requireAny(Permission.GUIDE_EDITOR);
        return guideService.create(input);
    }

    @MutationMapping
    public GuideOutputDTO updateGuide(@Argument Long id, @Argument("input") @Valid GuideInputDTO input) {
        requireAny(Permission.GUIDE_EDITOR);
        return guideService.update(id, input);
    }

    @MutationMapping
    public Boolean deleteGuide(@Argument Long id) {
        requireAny(Permission.GUIDE_EDITOR);
        return guideService.delete(id);
    }
}
