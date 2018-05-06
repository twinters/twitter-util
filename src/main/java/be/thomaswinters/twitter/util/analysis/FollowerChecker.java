package be.thomaswinters.twitter.util.analysis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FollowerChecker {

    private final Cache<Long, Relationship> cache;

    private final Twitter twitter;
    private final long userId;

    public FollowerChecker(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.userId = twitter.getId();
        this.cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();
    }

    private Relationship getRelationship(User user) {
        try {
            Relationship relationship = cache.get(
                    user.getId(),
                    () -> twitter.showFriendship(userId, user.getId()));

            return relationship;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failure to check relatation");
    }

    public boolean isMutuallyFollowing(User user) {
        Relationship relation = getRelationship(user);
        return relation.isSourceFollowedByTarget() && relation.isSourceFollowingTarget();
    }

    public boolean isFollowing(User user) {
        return getRelationship(user).isSourceFollowingTarget() || user.isFollowRequestSent();
    }

    public boolean isFollowedBy(User user) {
        return getRelationship(user).isSourceFollowedByTarget();
    }

    public void unfollow(User user) throws TwitterException {
        System.out.println("UNFOLLOWED: " + user.getScreenName());
        twitter.destroyFriendship(user.getId());
        cache.invalidate(user.getId());
    }

    public void follow(User user) throws TwitterException {
        System.out.println("STARTED FOLLOWING: " + user.getScreenName());
        twitter.createFriendship(user.getId());
        cache.invalidate(user.getId());
    }
}
