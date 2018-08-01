package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.generator.streamgenerator.reacting.IReactingStreamGenerator;
import com.google.common.collect.ImmutableList;
import twitter4j.Status;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class TweetsFetcherCombiner implements ITweetsFetcher {
    private final ImmutableList<IReactingStreamGenerator<Status, Long>> tweetRetrievers;

    public TweetsFetcherCombiner(Collection<? extends IReactingStreamGenerator<Status, Long>> tweetRetrievers) {
        this.tweetRetrievers = ImmutableList.copyOf(tweetRetrievers);
    }

    public TweetsFetcherCombiner(IReactingStreamGenerator<Status, Long>... tweetsFetchers) {
        this(Arrays.asList(tweetsFetchers));
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return tweetRetrievers.stream()
                .flatMap(retriever -> retriever.generateStream(sinceId));
    }
}
