package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.util.analysis.TwitterFollower;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.function.Predicate;

/**
 * Filter for filtering out tweets from users that are not following the current twitter user
 */
public class NotFollowingCurrentUserFilter implements Predicate<Status> {
    private final Twitter twitter;
    private final boolean unfollowAutomatically;
    private final TwitterFollower followStatusChecker;

    public NotFollowingCurrentUserFilter(Twitter twitter, boolean unfollowAutomatically) throws TwitterException {
        this.twitter = twitter;
        this.unfollowAutomatically = unfollowAutomatically;
        this.followStatusChecker = new TwitterFollower(twitter);
    }

    /**
     * Returns false (=not allowed) if the poster of the tweet is not following the current twitter user
     *
     * @param status
     * @return
     */
    @Override
    public boolean test(Status status) {
        User tweetPoster = status.getUser();

        if (followStatusChecker.isFollowedBy(tweetPoster)) {
            return true;
        }
        if (unfollowAutomatically && followStatusChecker.isFollowing(tweetPoster)) {
            try {
                System.out.println("I'm going to unfollow " + status.getUser() + " because he is not following us anymore");
                followStatusChecker.unfollow(tweetPoster);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
