package task.healthyhabits.exceptions;
import static org.assertj.core.api.Assertions.assertThat;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

import task.healthyhabits.exceptions.BusinessException;
import task.healthyhabits.exceptions.ErrorCode;
import task.healthyhabits.exceptions.GlobalExceptionHandler;

class GlobalExceptionHandlerTest {

    private final TestableGlobalExceptionHandler handler = new TestableGlobalExceptionHandler();
    private final DataFetchingEnvironment environment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment().build();

    @Test
    void handlesBusinessExceptionWithHint() {
        BusinessException exception = new BusinessException("duplicate", ErrorCode.DUPLICATE_FAVORITE, "add another");

        GraphQLError error = handler.invoke(exception, environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(error.getMessage()).isEqualTo("duplicate");
        assertThat(error.getExtensions())
                .containsEntry("code", ErrorCode.DUPLICATE_FAVORITE.name())
                .containsEntry("hint", "add another");
    }

    @Test
    void handlesBusinessExceptionWithoutHint() {
        BusinessException exception = new BusinessException("missing", ErrorCode.HABIT_NOT_FOUND);

        GraphQLError error = handler.invoke(exception, environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.HABIT_NOT_FOUND.name());
        assertThat(error.getExtensions()).doesNotContainKey("hint");
    }

    @Test
    void handlesBadCredentialsException() {
        GraphQLError error = handler.invoke(new BadCredentialsException("bad"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
        assertThat(error.getMessage()).isEqualTo("Authentication failed. Please verify your credentials.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.BAD_CREDENTIALS.name());
    }

    @Test
    void handlesAuthenticationException() {
        GraphQLError error = handler.invoke(new AuthenticationCredentialsNotFoundException("missing"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
         assertThat(error.getMessage()).isEqualTo("Authentication failed. Please verify your credentials.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.BAD_CREDENTIALS.name());
    }

    @Test
    void handlesAccessDeniedException() {
        GraphQLError error = handler.invoke(new AccessDeniedException("denied"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
        assertThat(error.getMessage()).isEqualTo("You do not have permission to perform this action.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.ACCESS_DENIED.name());
    }

    @Test
    void handlesConstraintViolationException() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = (ConstraintViolation<Object>) org.mockito.Mockito.mock(ConstraintViolation.class);
        Path path = org.mockito.Mockito.mock(Path.class);
        org.mockito.Mockito.when(path.toString()).thenReturn("field");
        org.mockito.Mockito.when(violation.getPropertyPath()).thenReturn(path);
        org.mockito.Mockito.when(violation.getMessage()).thenReturn("must not be blank");
        org.mockito.Mockito.when(violation.getInvalidValue()).thenReturn("");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        GraphQLError error = handler.invoke(exception, environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(error.getMessage()).isEqualTo("Request validation failed.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.CONSTRAINT_VIOLATION.name());
        List<Map<String, Object>> violations = (List<Map<String, Object>>) error.getExtensions().get("violations");
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0))
                .containsEntry("field", "field")
                .containsEntry("message", "must not be blank")
                .containsEntry("rejectedValue", "");
    }

    @Test
    void handlesDataIntegrityViolationException() {
        GraphQLError error = handler.invoke(new DataIntegrityViolationException("conflict"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(error.getMessage()).isEqualTo("Operation violates data integrity constraints.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.DATA_INTEGRITY_VIOLATION.name());
    }

    @Test
    void handlesIllegalArgumentWithMessage() {
        GraphQLError error = handler.invoke(new IllegalArgumentException("bad input"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(error.getMessage()).isEqualTo("bad input");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.ILLEGAL_ARGUMENT.name());
    }

    @Test
    void handlesIllegalArgumentWithoutMessage() {
        GraphQLError error = handler.invoke(new IllegalArgumentException(), environment);

        assertThat(error.getMessage()).isEqualTo("Invalid request.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.ILLEGAL_ARGUMENT.name());
    }

    @Test
    void handlesUnexpectedException() {
        GraphQLError error = handler.invoke(new RuntimeException("boom"), environment);

        assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(error.getExtensions()).containsEntry("code", ErrorCode.INTERNAL_ERROR.name());
        assertThat(error.getExtensions().get("id")).isInstanceOf(String.class);
        UUID.fromString((String) error.getExtensions().get("id"));
    }

    private static final class TestableGlobalExceptionHandler extends GlobalExceptionHandler {

        GraphQLError invoke(Throwable ex, DataFetchingEnvironment env) {
            return super.resolveToSingleError(ex, env);
        }
    }
}
