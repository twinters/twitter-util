package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.util.paging.PagingTweetFetcher;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractPagingTweetsFetcher implements ITweetsFetcher {

    private final Twitter twitter;

    protected AbstractPagingTweetsFetcher(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return new PagingTweetFetcher(this::getTweetsFromPage).getTweets(sinceId);
    }

    protected Twitter getTwitter() {
        return twitter;
    }

    protected abstract List<Status> getTweetsFromPage(Paging page);
}
