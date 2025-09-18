package task.healthyhabits.resolvers.guides;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.GuideDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.guide.GuideService;

import java.util.List;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class GuideQueryResolver {

    private final GuideService guideService;

    @QueryMapping
    public GuidePage listGuides(@Argument int page, @Argument int size) {
        requireAny(Permission.GUIDE_READ, Permission.GUIDE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<GuideDTO> p = guideService.list(pageable);
        return new GuidePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public GuidePage listGuidesByObjective(@Argument String objective, @Argument int page, @Argument int size) {
        requireAny(Permission.GUIDE_READ, Permission.GUIDE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<GuideDTO> p = guideService.listByObjective(objective, pageable);
        return new GuidePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public GuidePage listRecommendedGuides(@Argument Category category, @Argument Long forUserId,
                                           @Argument int page, @Argument int size) {
        requireAny(Permission.GUIDE_READ, Permission.GUIDE_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        Page<GuideDTO> p = guideService.recommended(category, forUserId, pageable);
        return new GuidePage(p.getContent(), p.getTotalPages(), (int) p.getTotalElements(), p.getSize(), p.getNumber());
    }

    @QueryMapping
    public GuideDTO getGuideById(@Argument Long id) {
        requireAny(Permission.GUIDE_READ, Permission.GUIDE_EDITOR);
        return guideService.findByIdOrNull(id);
    }

    public record GuidePage(List<GuideDTO> content, int totalPages, int totalElements, int size, int number) { }
}
