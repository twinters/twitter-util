package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.function.Predicate;

/**
 * Filter for filtering out tweets that has already been interacted with
 */
public class RandomFilter implements Predicate<Status> {

    // Large prime number
    private static final int MULTIPLIER_CALCULATOR_NUMBER = 617;

    // Larger prime number to make it jump randomly throughout the space
    // such that if id goes linearly up by one, it does not allow large chunks
    private static final int JUMPING_LARGE_PRIME = 104891;

    private final int chances;
    private final int outOf;
    private final long salt;

    public RandomFilter(Twitter twitter, int chances, int outOf) {
        if (twitter==null){
            throw new IllegalArgumentException("Twitter can't be null");
        }
        this.salt = TwitterUnchecker.uncheck(twitter::getId);

        // Multiply the chances and outOf by a random number, such that the random function uses a different periods
        int multiplier = (int) (this.salt % MULTIPLIER_CALCULATOR_NUMBER);
        this.chances = chances * multiplier;
        this.outOf = outOf * multiplier;
    }

    /**
     * Returns true in 'chances' out of 'outOf' times.
     */
    @Override
    public boolean test(Status status) {
        return test(status.getId());
    }

    public boolean test(long id) {
        long number = (((id * JUMPING_LARGE_PRIME + salt) % outOf )+ outOf)% outOf;
        return number < chances;

    }
}
