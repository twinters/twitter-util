package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import be.thomaswinters.twitter.userfetcher.FollowersFetcher;
import be.thomaswinters.twitter.util.analysis.FollowerChecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FollowBehaviour implements ITwitterBehaviour {

    private final FollowerChecker followerUtil;
    private final FollowersFetcher followersFetcher;

    public FollowBehaviour(Twitter twitter) throws TwitterException {
        this.followerUtil = new FollowerChecker(twitter);
        this.followersFetcher = new FollowersFetcher(twitter);
    }

    public void followNewFollowersBack(ITweeter tweeter) {
        try {
            List<User> toFollow = followersFetcher.fetchUsers()
                    .takeWhile(user -> !followerUtil.isFollowing(user))
                    .collect(Collectors.toList());
            
            // Reverse so we follow oldest new followers first
            Collections.reverse(toFollow);

            for (User user : toFollow) {
                tweeter.follow(user);
            }
        } catch (TwitterException | UncheckedTwitterException e) {
            e.printStackTrace();
        }

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