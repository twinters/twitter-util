package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.stream.Stream;

public class UserFilteredTweetsFetcher implements ITweetsFetcher {
    private final FilteredTweetsFetcher filteredFetcher;

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, long userIdToBlock) {
        this.filteredFetcher = new FilteredTweetsFetcher(innerFetcher, status -> status.getUser().getId() != userIdToBlock);
    }

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, User user) {
        this(innerFetcher, user.getId());
    }

    public UserFilteredTweetsFetcher(ITweetsFetcher innerFetcher, Twitter mainTwitter) throws TwitterException {
        this(innerFetcher, mainTwitter.getId());
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return filteredFetcher.retrieve(sinceId);
    }
}
