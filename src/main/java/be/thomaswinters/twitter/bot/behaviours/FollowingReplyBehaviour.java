package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.behaviours.IReplyBehaviour;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.util.analysis.FollowerChecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FollowingReplyBehaviour implements IReplyBehaviour {
    private final FollowerChecker followerUtil;

    public FollowingReplyBehaviour(Twitter twitter) throws TwitterException {
        this.followerUtil = new FollowerChecker(twitter);
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        User user = tweetToReply.getUser();
        if (!followerUtil.isFollowing(user)) {
            try {
                tweeter.follow(user);
                return true;
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
