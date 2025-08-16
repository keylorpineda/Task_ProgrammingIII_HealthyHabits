package task.healthyhabits.mappers;

import task.healthyhabits.models.Reminder;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.dtos.normals.ReminderDTO;

public class MapperForReminder {

    public static Reminder toModel(ReminderInputDTO input) {
        if (input == null) return null;

        Reminder reminder = new Reminder();
        reminder.setUser(MapperForUser.toModel(input.getUser()));
        reminder.setHabit(MapperForHabit.toModel(input.getHabit()));
        reminder.setTime(input.getTime());
        reminder.setFrequency(input.getFrequency());

        return reminder;
    }

    public static ReminderOutputDTO toOutput(Reminder model) {
        if (model == null) return null;

        return new ReminderOutputDTO(
            model.getId(),
            MapperForUser.toOutput(model.getUser()),
            MapperForHabit.toOutput(model.getHabit()),
            model.getTime(),
            model.getFrequency()
        );
    }

    public static ReminderDTO toDTO(Reminder model) {
        if (model == null) return null;

        return new ReminderDTO(
            model.getId(),
            MapperForUser.toDTO(model.getUser()),
            MapperForHabit.toDTO(model.getHabit()),
            model.getTime(),
            model.getFrequency()
        );
    }

    public static Reminder fromDTO(ReminderDTO dto) {
        if (dto == null) return null;

        Reminder reminder = new Reminder();
        reminder.setId(dto.getId());
        reminder.setUser(MapperForUser.fromDTO(dto.getUser()));
        reminder.setHabit(MapperForHabit.fromDTO(dto.getHabit()));
        reminder.setTime(dto.getTime());
        reminder.setFrequency(dto.getFrequency());

        return reminder;
    }
}
