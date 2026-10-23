package exception;

/** Thrown when user input is invalid (empty ID, negative price, duplicate ID ...). */
public class InvalidProductException extends Exception {
    public InvalidProductException(String message) {
        super(message);
    }
}
