package be.thomaswinters.twitter.util.retriever;

import be.thomaswinters.twitter.util.retriever.util.PagingTweetDownloader;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.stream.Stream;

public class MentionTweetsFetcher implements ITweetsFetcher {

    private final Twitter twitter;

    public MentionTweetsFetcher(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return new PagingTweetDownloader(this::getMentions).getTweets(sinceId);
    }

    private List<Status> getMentions(Paging page) {
        try {
            return twitter.getMentionsTimeline(page);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }
}
