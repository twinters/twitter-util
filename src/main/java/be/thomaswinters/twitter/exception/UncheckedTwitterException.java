package be.thomaswinters.twitter.exception;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Class that can be used to wrap checked TwitterException such that it can be used for lambdas
 */
public class UncheckedTwitterException extends RuntimeException implements TwitterResponse {
    private final TwitterException twitterException;

    public UncheckedTwitterException(TwitterException twitterException) {
        this.twitterException = twitterException;
    }

    public String getMessage() {
        return twitterException.getMessage();
    }

    public int getStatusCode() {
        return twitterException.getStatusCode();
    }

    public int getErrorCode() {
        return twitterException.getErrorCode();
    }

    public String getResponseHeader(String name) {
        return twitterException.getResponseHeader(name);
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return twitterException.getRateLimitStatus();
    }

    @Override
    public int getAccessLevel() {
        return twitterException.getAccessLevel();
    }

    public int getRetryAfter() {
        return twitterException.getRetryAfter();
    }

    public boolean isCausedByNetworkIssue() {
        return twitterException.isCausedByNetworkIssue();
    }

    public boolean exceededRateLimitation() {
        return twitterException.exceededRateLimitation();
    }

    public boolean resourceNotFound() {
        return twitterException.resourceNotFound();
    }

    public String getExceptionCode() {
        return twitterException.getExceptionCode();
    }

    public String getErrorMessage() {
        return twitterException.getErrorMessage();
    }

    public boolean isErrorMessageAvailable() {
        return twitterException.isErrorMessageAvailable();
    }

    public String getLocalizedMessage() {
        return twitterException.getLocalizedMessage();
    }

    public Throwable getCause() {
        return twitterException.getCause();
    }

    public Throwable initCause(Throwable cause) {
        return twitterException.initCause(cause);
    }

    public void printStackTrace() {
        twitterException.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        twitterException.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        twitterException.printStackTrace(s);
    }

    public Throwable fillInStackTrace() {
        return twitterException.fillInStackTrace();
    }

    public StackTraceElement[] getStackTrace() {
        return twitterException.getStackTrace();
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        twitterException.setStackTrace(stackTrace);
    }

}
