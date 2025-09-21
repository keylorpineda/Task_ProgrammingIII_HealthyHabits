package task.healthyhabits.exceptions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import task.healthyhabits.exceptions.BusinessException;
import task.healthyhabits.exceptions.ErrorCode;

class BusinessExceptionTest {

    @Test
    void constructorWithErrorCodeStoresDetailsWithoutHintOrCause() {
        BusinessException exception = new BusinessException("message", ErrorCode.INVALID_PASSWORD);

        assertThat(exception.getMessage()).isEqualTo("message");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        assertThat(exception.getHint()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructorWithHintPreservesHint() {
        BusinessException exception = new BusinessException("message", ErrorCode.HABIT_NOT_FOUND, "use different id");

        assertThat(exception.getHint()).isEqualTo("use different id");
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HABIT_NOT_FOUND);
    }

    @Test
    void constructorWithCauseAndBlankHintOmitsHint() {
        IllegalStateException cause = new IllegalStateException("failure");

        BusinessException exception = new BusinessException("message", ErrorCode.ACCESS_DENIED, "   ", cause);

        assertThat(exception.getHint()).isNull();
        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
    }
}