package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class FilteredTweetsFetcher implements ITweetsFetcher {
    public static final Predicate<Status> RETWEETS_REJECTER = status -> !status.isRetweet();
    public static final Predicate<Status> REPLY_REJECTER = status -> status.getInReplyToStatusId() > 0l;


    private final ITweetsFetcher innerFetcher;
    private final Predicate<Status> predicate;

    public FilteredTweetsFetcher(ITweetsFetcher innerFetcher, Predicate<Status> predicate) {
        this.innerFetcher = innerFetcher;
        this.predicate = predicate;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return innerFetcher.retrieve(sinceId)
                .filter(predicate);
    }
}
