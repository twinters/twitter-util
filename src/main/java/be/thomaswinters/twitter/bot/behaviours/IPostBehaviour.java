package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.Tweeter;

/**
 * Abstraction for a post behaviour a Twitterbot can have
 */
@FunctionalInterface
public interface IPostBehaviour {
    /**
     * Trt doing a posting action using the Tweeter object
     *
     * @param tweeter   The twitter connection to use, which is necessary as it notifies the action listeners
     * @return true if the posting action was successful, false otherwise.
     * This is especially important for behaviours used in a cascade or other composites
     */
    boolean post(Tweeter tweeter);
}
