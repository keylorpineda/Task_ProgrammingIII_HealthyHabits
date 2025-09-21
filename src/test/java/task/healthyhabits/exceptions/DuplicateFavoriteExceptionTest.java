package task.healthyhabits.exceptions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.DuplicateFavoriteException;
import task.healthyhabits.exceptions.ErrorCode;

class DuplicateFavoriteExceptionTest {

    @Test
    void defaultConstructorUsesDefaultMessageAndCode() {
        DuplicateFavoriteException exception = new DuplicateFavoriteException();

        assertThat(exception.getMessage()).isEqualTo("Habit is already in favorites.");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_FAVORITE);
        assertThat(exception.getHint()).isNull();
    }

    @Test
    void constructorsPreserveProvidedArguments() {
        Throwable cause = new IllegalStateException("duplicate");

        DuplicateFavoriteException messageOnly = new DuplicateFavoriteException("custom message");
        DuplicateFavoriteException withHint = new DuplicateFavoriteException("custom message", "try another");
        DuplicateFavoriteException withCause = new DuplicateFavoriteException("custom message", cause);
        DuplicateFavoriteException withHintAndCause = new DuplicateFavoriteException("custom message", "try another", cause);

        assertThat(messageOnly.getMessage()).isEqualTo("custom message");
        assertThat(messageOnly.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_FAVORITE);
        assertThat(messageOnly.getHint()).isNull();
        assertThat(messageOnly.getCause()).isNull();

        assertThat(withHint.getHint()).isEqualTo("try another");
        assertThat(withHint.getCause()).isNull();

        assertThat(withCause.getCause()).isSameAs(cause);
        assertThat(withCause.getHint()).isNull();

        assertThat(withHintAndCause.getHint()).isEqualTo("try another");
        assertThat(withHintAndCause.getCause()).isSameAs(cause);
    }
}
