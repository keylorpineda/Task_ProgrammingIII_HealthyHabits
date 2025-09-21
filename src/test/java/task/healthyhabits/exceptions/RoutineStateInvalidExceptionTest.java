package task.healthyhabits.exceptions;import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.ErrorCode;
import task.healthyhabits.exceptions.RoutineStateInvalidException;

class RoutineStateInvalidExceptionTest {

    @Test
    void defaultConstructorUsesDefaultMessage() {
        RoutineStateInvalidException exception = new RoutineStateInvalidException();

        assertThat(exception.getMessage()).isEqualTo("Routine cannot transition to the requested state.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROUTINE_STATE_INVALID);
        assertThat(exception.getHint()).isNull();
    }

    @Test
    void constructorsPropagateArguments() {
        Throwable cause = new IllegalStateException("invalid state");

        RoutineStateInvalidException messageOnly = new RoutineStateInvalidException("custom message");
        RoutineStateInvalidException withHint = new RoutineStateInvalidException("custom message", "review transition");
        RoutineStateInvalidException withCause = new RoutineStateInvalidException("custom message", cause);
        RoutineStateInvalidException withHintAndCause = new RoutineStateInvalidException("custom message", "review transition", cause);

        assertThat(messageOnly.getMessage()).isEqualTo("custom message");
        assertThat(messageOnly.getHint()).isNull();
        assertThat(messageOnly.getCause()).isNull();

        assertThat(withHint.getHint()).isEqualTo("review transition");
        assertThat(withHint.getCause()).isNull();

        assertThat(withCause.getCause()).isSameAs(cause);
        assertThat(withCause.getHint()).isNull();

        assertThat(withHintAndCause.getHint()).isEqualTo("review transition");
        assertThat(withHintAndCause.getCause()).isSameAs(cause);
    }
}