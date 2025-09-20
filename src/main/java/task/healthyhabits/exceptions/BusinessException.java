package task.healthyhabits.exceptions;

import java.util.Objects;

import org.springframework.util.StringUtils;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String hint;

    public BusinessException(String message, ErrorCode errorCode) {
        this(message, errorCode, null, null);
    }

    public BusinessException(String message, ErrorCode errorCode, String hint) {
        this(message, errorCode, hint, null);
    }

    public BusinessException(String message, ErrorCode errorCode, Throwable cause) {
        this(message, errorCode, null, cause);
    }

    public BusinessException(String message, ErrorCode errorCode, String hint, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
        this.hint = StringUtils.hasText(hint) ? hint : null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getHint() {
        return hint;
    }
}