package be.thomaswinters.twitter.userfetcher;

import be.thomaswinters.generator.streamgenerator.IStreamGenerator;
import twitter4j.User;

import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

public interface IUserFetcher extends IStreamGenerator<User> {
    Stream<User> fetchUsers();

    default IUserFetcher cacheFor(TemporalAmount temporalAmount) {
        return new CachedUserFetcher(this, temporalAmount);
    }

    default Stream<User> generateStream() {
        return fetchUsers();
    }

}
