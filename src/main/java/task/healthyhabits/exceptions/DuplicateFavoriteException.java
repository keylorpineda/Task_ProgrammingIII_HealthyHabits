package task.healthyhabits.exceptions;

/**
 * Indicates a user attempted to favorite a habit that is already marked as a favorite.
 */
public class DuplicateFavoriteException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Habit is already in favorites.";

    public DuplicateFavoriteException() {
        this(DEFAULT_MESSAGE);
    }

    public DuplicateFavoriteException(String message) {
        super(message, ErrorCode.DUPLICATE_FAVORITE);
    }

    public DuplicateFavoriteException(String message, String hint) {
        super(message, ErrorCode.DUPLICATE_FAVORITE, hint);
    }

    public DuplicateFavoriteException(String message, Throwable cause) {
        super(message, ErrorCode.DUPLICATE_FAVORITE, cause);
    }

    public DuplicateFavoriteException(String message, String hint, Throwable cause) {
        super(message, ErrorCode.DUPLICATE_FAVORITE, hint, cause);
    }
}