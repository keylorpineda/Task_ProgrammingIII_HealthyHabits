package task.healthyhabits.exceptions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.ErrorCode;
import task.healthyhabits.exceptions.InvalidPasswordException;

class InvalidPasswordExceptionTest {

    @Test
    void defaultConstructorProvidesDefaultMessageAndHint() {
        InvalidPasswordException exception = new InvalidPasswordException();

        assertThat(exception.getMessage()).isEqualTo("Password does not meet the required complexity.");
        assertThat(exception.getHint()).isEqualTo("Ensure the password satisfies length and character requirements.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

    @Test
    void constructorsPropagateArguments() {
        Throwable cause = new IllegalStateException("invalid");

        InvalidPasswordException messageOnly = new InvalidPasswordException("custom message");
        InvalidPasswordException withHint = new InvalidPasswordException("custom message", "provide special chars");
        InvalidPasswordException withCause = new InvalidPasswordException("custom message", cause);
        InvalidPasswordException withHintAndCause = new InvalidPasswordException("custom message", "provide special chars", cause);

        assertThat(messageOnly.getMessage()).isEqualTo("custom message");
        assertThat(messageOnly.getHint()).isNull();
        assertThat(messageOnly.getCause()).isNull();

        assertThat(withHint.getHint()).isEqualTo("provide special chars");
        assertThat(withHint.getCause()).isNull();

        assertThat(withCause.getCause()).isSameAs(cause);
        assertThat(withCause.getHint()).isNull();

        assertThat(withHintAndCause.getHint()).isEqualTo("provide special chars");
        assertThat(withHintAndCause.getCause()).isSameAs(cause);
    }
}
