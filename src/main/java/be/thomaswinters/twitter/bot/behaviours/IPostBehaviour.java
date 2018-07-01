package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.selection.Weighted;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.util.Arrays;

/**
 * Abstraction for a post behaviour a Twitterbot can have
 */
@FunctionalInterface
public interface IPostBehaviour {
    /**
     * Trt doing a posting action using the Tweeter object
     *
     * @param tweeter The twitter connection to use, which is necessary as it notifies the action listeners
     * @return true if the posting action was successful, false otherwise.
     * This is especially important for behaviours used in a cascade or other composites
     */
    boolean post(ITweeter tweeter);

    default IPostBehaviour orElse(IPostBehaviour postBehaviour) {
        return new PostBehaviourChain(Arrays.asList(this, postBehaviour));
    }
    default IPostBehaviour and(IPostBehaviour postBehaviour) {
        return new PostBehaviourConjunction(Arrays.asList(this, postBehaviour));
    }

    default IPostBehaviour retry(int amountOfTimes) {
        return new RetryingPostBehaviour(this, amountOfTimes);
    }

    default Weighted<? extends IPostBehaviour> weight(double weight) {
        return new Weighted<>(this, weight);
    }
}
