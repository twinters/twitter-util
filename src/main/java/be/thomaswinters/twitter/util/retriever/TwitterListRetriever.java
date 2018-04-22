package be.thomaswinters.twitter.util.retriever;

import be.thomaswinters.twitter.util.retriever.util.PagingTweetDownloader;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.stream.Stream;

public class TwitterListRetriever implements ITweetRetriever {

    private final Twitter twitter;
    private final long list;

    public TwitterListRetriever(Twitter twitter, long list) {
        this.twitter = twitter;
        this.list = list;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        return new PagingTweetDownloader(page -> getListTweets(list, page)).getTweets(sinceId);
    }

    private List<Status> getListTweets(long id, Paging page) {
        try {
            return twitter.getUserListStatuses(list, page);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }
}
