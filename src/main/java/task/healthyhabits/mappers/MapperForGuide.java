package task.healthyhabits.mappers;

import task.healthyhabits.models.Guide;
import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.dtos.normals.GuideDTO;

import java.util.stream.Collectors;

public class MapperForGuide {

    public static Guide toModel(GuideInputDTO input) {
        if (input == null) return null;

        Guide guide = new Guide();
        guide.setTitle(input.getTitle());
        guide.setContent(input.getContent());
        guide.setCategory(input.getCategory());

        guide.setRecommendedFor(
            input.getRecommendedFor().stream()
                .map(MapperForHabit::toModel)
                .collect(Collectors.toList())
        );

        return guide;
    }

    public static GuideOutputDTO toOutput(Guide model) {
        if (model == null) return null;

        return new GuideOutputDTO(
            model.getId(),
            model.getTitle(),
            model.getContent(),
            model.getCategory(),
            model.getRecommendedFor().stream()
                .map(MapperForHabit::toOutput)
                .collect(Collectors.toList())
        );
    }

    public static GuideDTO toDTO(Guide model) {
        if (model == null) return null;

        return new GuideDTO(
            model.getId(),
            model.getTitle(),
            model.getContent(),
            model.getCategory(),
            model.getRecommendedFor().stream()
                .map(MapperForHabit::toDTO)
                .collect(Collectors.toList())
        );
    }

    public static Guide fromDTO(GuideDTO dto) {
        if (dto == null) return null;

        Guide guide = new Guide();
        guide.setId(dto.getId());
        guide.setTitle(dto.getTitle());
        guide.setContent(dto.getContent());
        guide.setCategory(dto.getCategory());

        guide.setRecommendedFor(
            dto.getRecommendedFor().stream()
                .map(MapperForHabit::fromDTO)
                .collect(Collectors.toList())
        );

        return guide;
    }
}
