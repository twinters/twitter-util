package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.selection.Weighted;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;

import java.util.Arrays;

/**
 * Abstraction for a reply behaviour a Twitterbot can have:
 */
@FunctionalInterface
public interface IReplyBehaviour {
    /**
     * Trt doing a replying action using the Tweeter object
     *
     * @param tweeter The twitter connection to use, which is necessary as it notifies the action listeners
     * @return true if the replying action was successful, false otherwise.
     * * This is especially important for behaviours used in a cascade or other composites
     */
    boolean reply(ITweeter tweeter, Status tweetToReply);

    default IReplyBehaviour orElse(IReplyBehaviour replyBehaviour) {
        return new ReplyBehaviourChain(Arrays.asList(this, replyBehaviour));
    }

    default IReplyBehaviour and(IReplyBehaviour replyBehaviour) {
        return new ReplyBehaviourConjunction(Arrays.asList(this, replyBehaviour));
    }

    default IReplyBehaviour retry(int amountOfTimes) {
        return new RetryingReplyBehaviour(this, amountOfTimes);
    }

    default Weighted<? extends IReplyBehaviour> weight(double weight) {
        return new Weighted<>(this, weight);
    }
}
