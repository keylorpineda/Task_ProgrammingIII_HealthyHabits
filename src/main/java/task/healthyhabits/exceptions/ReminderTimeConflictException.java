package task.healthyhabits.exceptions;

/**
 * Thrown when a reminder cannot be scheduled because another reminder overlaps its time.
 */
public class ReminderTimeConflictException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Reminder time conflicts with an existing reminder.";

    public ReminderTimeConflictException() {
        this(DEFAULT_MESSAGE);
    }

    public ReminderTimeConflictException(String message) {
        super(message, ErrorCode.REMINDER_TIME_CONFLICT);
    }

    public ReminderTimeConflictException(String message, String hint) {
        super(message, ErrorCode.REMINDER_TIME_CONFLICT, hint);
    }

    public ReminderTimeConflictException(String message, Throwable cause) {
        super(message, ErrorCode.REMINDER_TIME_CONFLICT, cause);
    }

    public ReminderTimeConflictException(String message, String hint, Throwable cause) {
        super(message, ErrorCode.REMINDER_TIME_CONFLICT, hint, cause);
    }
}