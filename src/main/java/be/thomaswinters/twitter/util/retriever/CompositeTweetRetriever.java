package be.thomaswinters.twitter.util.retriever;

import com.google.common.collect.ImmutableList;
import twitter4j.Status;

import java.util.Collection;
import java.util.stream.Stream;

public class CompositeTweetRetriever implements ITweetRetriever {
    private final ImmutableList<ITweetRetriever> tweetRetrievers;

    public CompositeTweetRetriever(Collection<? extends ITweetRetriever> tweetRetrievers) {
        this.tweetRetrievers = ImmutableList.copyOf(tweetRetrievers);
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return tweetRetrievers.stream()
                .flatMap(retriever -> retriever.retrieve(sinceId));
    }
}
