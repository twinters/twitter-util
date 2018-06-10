package be.thomaswinters.twitter.exception;

import org.jetbrains.annotations.NotNull;
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

}
