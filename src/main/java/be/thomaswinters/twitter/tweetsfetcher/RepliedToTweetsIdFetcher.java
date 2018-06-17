package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.generator.streamgenerator.reacting.IReactingStreamGenerator;
import twitter4j.Status;

import java.util.stream.Stream;

/**
 * This class maps a stream of tweets to the tweet they replied to.
 */
public class RepliedToTweetsIdFetcher implements IReactingStreamGenerator<Long, Long> {

    private final ITweetsFetcher innerTweetsFetcher;

    public RepliedToTweetsIdFetcher(ITweetsFetcher innerTweetsFetcher) {
        this.innerTweetsFetcher = innerTweetsFetcher;
    }

    @Override
    public Stream<Long> generateStream(Long sinceId) {
        return innerTweetsFetcher.retrieve(sinceId)
                .map(Status::getInReplyToStatusId)
                .filter(e -> e > 1);
    }
}
