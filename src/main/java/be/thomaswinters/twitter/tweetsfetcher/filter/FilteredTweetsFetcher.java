package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;

import java.util.function.Predicate;

public class FilteredTweetsFetcher extends AbstractFilteredTweetsFetcher {

    public static final Predicate<Status> RETWEETS_REJECTER = status -> !status.isRetweet();
    public static final Predicate<Status> REPLY_REJECTER = status -> status.getInReplyToStatusId() > 0l;
    private final Predicate<Status> predicate;

    public FilteredTweetsFetcher(ITweetsFetcher innerFetcher, Predicate<Status> predicate) {
        super(innerFetcher);
        this.predicate = predicate;
    }

    @Override
    public boolean isAllowed(Status status) {
        return predicate.test(status);
    }
}
