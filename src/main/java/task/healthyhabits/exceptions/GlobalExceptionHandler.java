package task.healthyhabits.exceptions;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import graphql.GraphqlErrorBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
         if (ex instanceof BusinessException businessException) {
            return handleBusinessException(businessException, env);
        }
        if (ex instanceof BadCredentialsException badCredentialsException) {
            return handleAuthenticationException(badCredentialsException, env);
        }
        if (ex instanceof AuthenticationException authenticationException) {
            return handleAuthenticationException(authenticationException, env);
        }
        if (ex instanceof AccessDeniedException) {
            return handleAccessDenied(env);
        }
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolation(constraintViolationException, env);
        }
        if (ex instanceof DataIntegrityViolationException) {
            return handleDataIntegrityViolation(env);
        }
        if (ex instanceof IllegalArgumentException illegalArgumentException) {
            return handleIllegalArgument(illegalArgumentException, env);
        }
        return handleUnexpected(ex, env);
    }

    private GraphQLError handleBusinessException(BusinessException exception, DataFetchingEnvironment env) {
        Map<String, Object> extensions = buildBusinessExtensions(exception);
        return GraphqlErrorBuilder.newError(env)
                .errorType(mapBusinessErrorType(exception.getErrorCode()))
                .message(exception.getMessage())
                .extensions(extensions)
                .build();
    }

    private Map<String, Object> buildBusinessExtensions(BusinessException exception) {
        String hint = exception.getHint();
        if (StringUtils.hasText(hint)) {
            return Map.of("code", exception.getErrorCode().name(), "hint", hint);
        }
        return Map.of("code", exception.getErrorCode().name());
    }

    private ErrorType mapBusinessErrorType(ErrorCode errorCode) {
        return switch (errorCode) {
            case HABIT_NOT_FOUND -> ErrorType.NOT_FOUND;
            case INVALID_PASSWORD, DUPLICATE_FAVORITE, REMINDER_TIME_CONFLICT, ROUTINE_STATE_INVALID -> ErrorType.BAD_REQUEST;
            default -> ErrorType.BAD_REQUEST;
        };
    }

    private GraphQLError handleAuthenticationException(AuthenticationException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.UNAUTHORIZED)
                .message("Authentication failed. Please verify your credentials.")
                .extensions(Map.of("code", ErrorCode.BAD_CREDENTIALS.name()))
                .build();
    }

    private GraphQLError handleAccessDenied(DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.FORBIDDEN)
                .message("You do not have permission to perform this action.")
                .extensions(Map.of("code", ErrorCode.ACCESS_DENIED.name()))
                .build();
    }

    private GraphQLError handleConstraintViolation(ConstraintViolationException exception, DataFetchingEnvironment env) {
        List<Map<String, Object>> violations = exception.getConstraintViolations().stream()
                .map(this::toViolationDetail)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Request validation failed.")
                .extensions(Map.of("code", ErrorCode.CONSTRAINT_VIOLATION.name(), "violations", violations))
                .build();
    }

    private Map<String, Object> toViolationDetail(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
        String message = StringUtils.hasText(violation.getMessage()) ? violation.getMessage() : "Validation failed";
        String rejectedValue = String.valueOf(violation.getInvalidValue());
        return Map.ofEntries(
                Map.entry("field", field),
                Map.entry("message", message),
                Map.entry("rejectedValue", rejectedValue));
    }

    private GraphQLError handleDataIntegrityViolation(DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Operation violates data integrity constraints.")
                .extensions(Map.of("code", ErrorCode.DATA_INTEGRITY_VIOLATION.name()))
                .build();
    }

    private GraphQLError handleIllegalArgument(IllegalArgumentException ex, DataFetchingEnvironment env) {
        String message = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "Invalid request.";
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(message)
                .extensions(Map.of("code", ErrorCode.ILLEGAL_ARGUMENT.name()))
                .build();
    }

    private GraphQLError handleUnexpected(Throwable ex, DataFetchingEnvironment env) {
        String errorId = UUID.randomUUID().toString();
        LOGGER.error("Unhandled exception with id {}", errorId, ex);
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred. Please try again later.")
                .extensions(Map.ofEntries(
                        Map.entry("code", ErrorCode.INTERNAL_ERROR.name()),
                        Map.entry("id", errorId)))
                .build();
    }
}