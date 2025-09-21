package task.healthyhabits.exceptions;

public class RoutineStateInvalidException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Routine cannot transition to the requested state.";

    public RoutineStateInvalidException() {
        this(DEFAULT_MESSAGE);
    }

    public RoutineStateInvalidException(String message) {
        super(message, ErrorCode.ROUTINE_STATE_INVALID);
    }

    public RoutineStateInvalidException(String message, String hint) {
        super(message, ErrorCode.ROUTINE_STATE_INVALID, hint);
    }

    public RoutineStateInvalidException(String message, Throwable cause) {
        super(message, ErrorCode.ROUTINE_STATE_INVALID, cause);
    }

    public RoutineStateInvalidException(String message, String hint, Throwable cause) {
        super(message, ErrorCode.ROUTINE_STATE_INVALID, hint, cause);
    }
}