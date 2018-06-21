package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.generator.streamgenerator.reacting.IReactingStreamGenerator;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.function.Predicate;

public class AlreadyRepliedToByOthersFilter implements Predicate<Status> {
    private final IReactingStreamGenerator<Long, Long> repliedToIdFetcher;

    public AlreadyRepliedToByOthersFilter(ITweetsFetcher tweetsFetcher, TemporalAmount cacheDuration) {
        this.repliedToIdFetcher = tweetsFetcher
                .cache(cacheDuration)
                .mapToRepliedToIds();
//                .seed(() -> TwitterUnchecker.uncheck(
//                        TwitterAnalysisUtil::getLastReplyStatus, twitter)
//                        .map(Status::getId).orElse(1L));
    }

    public AlreadyRepliedToByOthersFilter(ITweetsFetcher tweetsFetcher) {
        this(tweetsFetcher, Duration.ofMinutes(5));
    }

    private boolean hasBeenRepliedToByOthers(Status status) {
        return repliedToIdFetcher.generateStream(status.getId())
                .anyMatch(id -> id.equals(status.getId()));
    }

    @Override
    public boolean test(Status status) {
        return !hasBeenRepliedToByOthers(status);
    }

}
