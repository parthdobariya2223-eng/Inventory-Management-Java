package exception;

/** Thrown when we try to sell more than the available stock. */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
