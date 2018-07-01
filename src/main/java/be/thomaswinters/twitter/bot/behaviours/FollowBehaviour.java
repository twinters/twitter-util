package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.behaviours.ITwitterBehaviour;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import be.thomaswinters.twitter.userfetcher.FollowersFetcher;
import be.thomaswinters.twitter.util.analysis.FollowerChecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class FollowBehaviour implements ITwitterBehaviour {

    private final FollowerChecker followerUtil;
    private final FollowersFetcher followersFetcher;

    public FollowBehaviour(Twitter twitter) throws TwitterException {
        this.followerUtil = new FollowerChecker(twitter);
        this.followersFetcher = new FollowersFetcher(twitter);
    }

    public void followNewFollowersBack(ITweeter tweeter) {
        followersFetcher.fetchUsers()
                .takeWhile(user -> !followerUtil.isFollowing(user))
                .forEach(user -> TwitterUnchecker.uncheckConsumer(tweeter::follow, user));
    }

    /**
     * Automatically follows everyone from the reply list
     *
     * @param tweeter      The twitter connection to use, which is necessary as it notifies the action listeners
     * @param tweetToReply
     * @return
     */
    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        try {
            if (!followerUtil.isFollowing(tweetToReply.getUser())) {
                tweeter.follow(tweetToReply.getUser());
            }
            return true;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Automatically follows new followers
     *
     * @param tweeter The twitter connection to use, which is necessary as it notifies the action listeners
     * @return True if following everyone was successfull
     */
    @Override
    public boolean post(ITweeter tweeter) {
        try {
            followNewFollowersBack(tweeter);
            return true;
        } catch (UncheckedTwitterException e) {
            e.printStackTrace();
            return false;
//            throw new UncheckedTwitterException(e);
        }
    }
}
