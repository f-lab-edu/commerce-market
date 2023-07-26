package flab.commercemarket.common.exception;

public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message) {
        super(message);
    }

    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
