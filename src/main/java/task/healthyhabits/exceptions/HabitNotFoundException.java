package task.healthyhabits.exceptions;


public class HabitNotFoundException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Habit not found.";

    public HabitNotFoundException() {
        this(DEFAULT_MESSAGE);
    }

    public HabitNotFoundException(String message) {
        super(message, ErrorCode.HABIT_NOT_FOUND);
    }

    public HabitNotFoundException(String message, String hint) {
        super(message, ErrorCode.HABIT_NOT_FOUND, hint);
    }

    public HabitNotFoundException(String message, Throwable cause) {
        super(message, ErrorCode.HABIT_NOT_FOUND, cause);
    }

    public HabitNotFoundException(String message, String hint, Throwable cause) {
        super(message, ErrorCode.HABIT_NOT_FOUND, hint, cause);
    }
}