package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserFilteredTweetsFetcher extends AbstractFilteredTweetsFetcher {
    private final long userId;

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, long userId) {
        super(innerFetcher);
        this.userId = userId;
    }

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, User user) {
        this(innerFetcher, user.getId());
    }

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, Twitter mainTwitter) throws TwitterException {
        this(innerFetcher, mainTwitter.getId());
    }

    @Override
    public boolean isAllowed(Status status) {
        return status.getUser().getId() != userId;
    }
}
