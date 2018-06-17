package be.thomaswinters.twitter.tweetsfetcher;

import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetsFetcherCache implements ITweetsFetcher {

    private final ITweetsFetcher innerTweetsFetcher;
    private final TemporalAmount cacheTime;

    private List<Status> cache;
    private LocalDateTime lastCached;
    private long lastSinceId;

    public TweetsFetcherCache(ITweetsFetcher innerTweetsFetcher, TemporalAmount cacheTime) {
        this.innerTweetsFetcher = innerTweetsFetcher;
        this.cacheTime = cacheTime;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        if (lastCached == null || sinceId < lastSinceId || LocalDateTime.now().minus(cacheTime).isAfter(lastCached)) {
            cache = innerTweetsFetcher.retrieve(sinceId).collect(Collectors.toList());
            lastSinceId = sinceId;
            lastCached = LocalDateTime.now();
        }
        return cache.stream();

    }
}
