package task.healthyhabits.exceptions;

/**
 * Enumerates stable identifiers for GraphQL error responses.
 */
public enum ErrorCode {

    // Authentication & Authorization
    BAD_CREDENTIALS,
    ACCESS_DENIED,
    INVALID_PASSWORD,

    // Domain
    HABIT_NOT_FOUND,
    DUPLICATE_FAVORITE,
    REMINDER_TIME_CONFLICT,
    ROUTINE_STATE_INVALID,

    // Validation & Arguments
    CONSTRAINT_VIOLATION,
    ILLEGAL_ARGUMENT,

    // Persistence
    DATA_INTEGRITY_VIOLATION,

    // Generic
    INTERNAL_ERROR;
}