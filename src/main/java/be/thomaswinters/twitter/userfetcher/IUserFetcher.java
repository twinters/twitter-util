package be.thomaswinters.twitter.userfetcher;

import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

public interface IUserFetcher {
    Stream<Long> fetchUserIds();

    default IUserFetcher cacheFor(TemporalAmount temporalAmount) {
        return new CachedUserFetcher(this, temporalAmount);
    }
}
