package task.healthyhabits.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.ErrorCode;
import task.healthyhabits.exceptions.HabitNotFoundException;

class HabitNotFoundExceptionTest {

    @Test
    void defaultConstructorUsesDefaultMessage() {
        HabitNotFoundException exception = new HabitNotFoundException();

        assertThat(exception.getMessage()).isEqualTo("Habit not found.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HABIT_NOT_FOUND);
        assertThat(exception.getHint()).isNull();
    }

    @Test
    void constructorsPropagateArguments() {
        Throwable cause = new IllegalStateException("missing");

        HabitNotFoundException messageOnly = new HabitNotFoundException("custom message");
        HabitNotFoundException withHint = new HabitNotFoundException("custom message", "check id");
        HabitNotFoundException withCause = new HabitNotFoundException("custom message", cause);
        HabitNotFoundException withHintAndCause = new HabitNotFoundException("custom message", "check id", cause);

        assertThat(messageOnly.getMessage()).isEqualTo("custom message");
        assertThat(messageOnly.getErrorCode()).isEqualTo(ErrorCode.HABIT_NOT_FOUND);
        assertThat(messageOnly.getHint()).isNull();
        assertThat(messageOnly.getCause()).isNull();

        assertThat(withHint.getHint()).isEqualTo("check id");
        assertThat(withHint.getCause()).isNull();

        assertThat(withCause.getCause()).isSameAs(cause);
        assertThat(withCause.getHint()).isNull();

        assertThat(withHintAndCause.getHint()).isEqualTo("check id");
        assertThat(withHintAndCause.getCause()).isSameAs(cause);
    }
}
