package be.thomaswinters.twitter.exception;

public class ExcessiveTweetLengthException extends RuntimeException {
    private final String message;

    public ExcessiveTweetLengthException(String message) {
        super("The given tweet's length is too large: " + message);
        this.message = message;
    }
}
