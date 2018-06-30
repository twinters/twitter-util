package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.behaviours.IReplyBehaviour;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

/**
 * Behaviour that likes all incoming posts
 */
public class LikingBehaviour implements IReplyBehaviour {
    private final Twitter twitter;

    public LikingBehaviour(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        if (!tweetToReply.isFavorited()) {
            try {
                tweeter.like(tweetToReply);
                return true;
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
