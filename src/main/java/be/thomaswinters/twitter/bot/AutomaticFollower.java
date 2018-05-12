package be.thomaswinters.twitter.bot;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import be.thomaswinters.twitter.userfetcher.FollowersFetcher;
import be.thomaswinters.twitter.util.analysis.FollowerChecker;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

public class AutomaticFollower implements IGenerator<String> {

    private final Twitter twitter;
    private final FollowerChecker followerUtil;
    private final FollowersFetcher followersFetcher;

    public AutomaticFollower(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.followerUtil = new FollowerChecker(twitter);
        this.followersFetcher = new FollowersFetcher(twitter);
    }

    public void followNewFollowersBack() throws TwitterException {
        followersFetcher.fetchUsers()
                .takeWhile(user -> !followerUtil.isFollowing(user))
                .forEach(user -> TwitterUnchecker.uncheckConsumer(followerUtil::follow, user));
    }

    @Override
    public Optional<String> generate() {
        try {
            followNewFollowersBack();
        } catch (TwitterException e) {
            throw new UncheckedTwitterException(e);
        }
        return Optional.empty();
    }
}
