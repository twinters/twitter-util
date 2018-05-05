package be.thomaswinters.twitter.userfetcher;

import twitter4j.User;

import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

public interface IUserFetcher {
    Stream<User> fetchUsers();

    default IUserFetcher cacheFor(TemporalAmount temporalAmount) {
        return new CachedUserFetcher(this, temporalAmount);
    }
}
