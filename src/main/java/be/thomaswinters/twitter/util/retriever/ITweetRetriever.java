package be.thomaswinters.twitter.util.retriever;

import twitter4j.Status;

import java.util.stream.Stream;

public interface ITweetRetriever {
    Stream<Status> retrieve(long sinceId);

    default Stream<Status> retriever() {
        return retrieve(0);
    }
}
