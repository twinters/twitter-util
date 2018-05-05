package be.thomaswinters.twitter.util.retriever;

import twitter4j.Status;

import java.util.stream.Stream;

public interface ITweetsFetcher {
    Stream<Status> retrieve(long sinceId);

    default Stream<Status> retrieve() {
        return retrieve(1l);
    }
}
