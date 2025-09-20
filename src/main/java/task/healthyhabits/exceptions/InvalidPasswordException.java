package task.healthyhabits.exceptions;


public class InvalidPasswordException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Password does not meet the required complexity.";
    private static final String DEFAULT_HINT = "Ensure the password satisfies length and character requirements.";

    public InvalidPasswordException() {
        super(DEFAULT_MESSAGE, ErrorCode.INVALID_PASSWORD, DEFAULT_HINT);
    }

    public InvalidPasswordException(String message) {
        super(message, ErrorCode.INVALID_PASSWORD);
    }

    public InvalidPasswordException(String message, String hint) {
        super(message, ErrorCode.INVALID_PASSWORD, hint);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, ErrorCode.INVALID_PASSWORD, cause);
    }

    public InvalidPasswordException(String message, String hint, Throwable cause) {
        super(message, ErrorCode.INVALID_PASSWORD, hint, cause);
    }
}
