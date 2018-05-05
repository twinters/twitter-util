package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;

import java.util.stream.Stream;

public abstract class AbstractFilteredTweetsFetcher implements ITweetsFetcher {


    private final ITweetsFetcher innerFetcher;

    public AbstractFilteredTweetsFetcher(ITweetsFetcher innerFetcher) {
        this.innerFetcher = innerFetcher;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return innerFetcher.retrieve(sinceId)
                .filter(this::isAllowed);
    }

    public abstract boolean isAllowed(Status status);
}
