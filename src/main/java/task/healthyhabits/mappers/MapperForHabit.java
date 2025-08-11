package task.healthyhabits.mappers;

import task.healthyhabits.models.Habit;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;

public class MapperForHabit {

    public static Habit toModel(HabitInputDTO input) {
        if (input == null) return null;

        Habit habit = new Habit();
        habit.setName(input.getName());
        habit.setCategory(input.getCategory());
        habit.setDescription(input.getDescription());
        return habit;
    }

    public static HabitDTO toDTO(Habit model) {
        if (model == null) return null;

        return new HabitDTO(
            model.getId(),
            model.getName(),
            model.getCategory(),
            model.getDescription()
        );
    }

    public static HabitOutputDTO toOutput(Habit model) {
        if (model == null) return null;

        return new HabitOutputDTO(
            model.getId(),
            model.getName(),
            model.getCategory(),
            model.getDescription()
        );
    }

    public static Habit fromDTO(HabitDTO dto) {
        if (dto == null) return null;

        Habit habit = new Habit();
        habit.setId(dto.getId());
        habit.setName(dto.getName());
        habit.setCategory(dto.getCategory());
        habit.setDescription(dto.getDescription());
        return habit;
    }
}
