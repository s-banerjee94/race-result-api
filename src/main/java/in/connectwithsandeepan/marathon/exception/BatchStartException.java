package in.connectwithsandeepan.marathon.exception;

public class BatchStartException extends RuntimeException {
    public BatchStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchStartException(Throwable cause) {
        super(cause);
    }
}