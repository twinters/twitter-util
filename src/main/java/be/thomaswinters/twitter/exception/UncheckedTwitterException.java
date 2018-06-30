package be.thomaswinters.twitter.exception;

import org.jetbrains.annotations.NotNull;
import twitter4j.HttpResponseCode;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Class that can be used to wrap checked TwitterException such that it can be used for lambdas
 */
public class UncheckedTwitterException extends RuntimeException implements TwitterResponse, HttpResponseCode {
    private final TwitterException twitterException;

    public UncheckedTwitterException(@NotNull TwitterException twitterException) {
        super(twitterException);
        this.twitterException = twitterException;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return twitterException.getRateLimitStatus();
    }

    @Override
    public int getAccessLevel() {
        return twitterException.getAccessLevel();
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
}
