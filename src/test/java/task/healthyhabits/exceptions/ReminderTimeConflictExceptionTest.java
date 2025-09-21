package task.healthyhabits.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.ErrorCode;
import task.healthyhabits.exceptions.ReminderTimeConflictException;

class ReminderTimeConflictExceptionTest {

    @Test
    void defaultConstructorUsesDefaultMessage() {
        ReminderTimeConflictException exception = new ReminderTimeConflictException();

        assertThat(exception.getMessage()).isEqualTo("Reminder time conflicts with an existing reminder.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REMINDER_TIME_CONFLICT);
        assertThat(exception.getHint()).isNull();
    }

    @Test
    void constructorsPropagateArguments() {
        Throwable cause = new IllegalStateException("conflict");

        ReminderTimeConflictException messageOnly = new ReminderTimeConflictException("custom message");
        ReminderTimeConflictException withHint = new ReminderTimeConflictException("custom message", "choose different time");
        ReminderTimeConflictException withCause = new ReminderTimeConflictException("custom message", cause);
        ReminderTimeConflictException withHintAndCause = new ReminderTimeConflictException("custom message", "choose different time", cause);

        assertThat(messageOnly.getMessage()).isEqualTo("custom message");
        assertThat(messageOnly.getHint()).isNull();
        assertThat(messageOnly.getCause()).isNull();

        assertThat(withHint.getHint()).isEqualTo("choose different time");
        assertThat(withHint.getCause()).isNull();

        assertThat(withCause.getCause()).isSameAs(cause);
        assertThat(withCause.getHint()).isNull();

        assertThat(withHintAndCause.getHint()).isEqualTo("choose different time");
        assertThat(withHintAndCause.getCause()).isSameAs(cause);
    }
}
