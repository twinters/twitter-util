package be.thomaswinters.twitter.tweetsfetcher;

import com.google.common.collect.ImmutableList;
import twitter4j.Status;

import java.util.Collection;
import java.util.stream.Stream;

public class TweetsFetcherCombiner implements ITweetsFetcher {
    private final ImmutableList<ITweetsFetcher> tweetRetrievers;

    public TweetsFetcherCombiner(Collection<? extends ITweetsFetcher> tweetRetrievers) {
        this.tweetRetrievers = ImmutableList.copyOf(tweetRetrievers);
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return tweetRetrievers.stream()
                .flatMap(retriever -> retriever.retrieve(sinceId));
    }
}
