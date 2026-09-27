/**
 * Custom checked exception thrown whenever user input fails validation.
 * Carries a human-readable message that is shown directly to the user.
 */
public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
