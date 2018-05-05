package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.tweetsfetcher.util.PagingTweetDownloader;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.stream.Stream;

public class ListTweetsFetcher implements ITweetsFetcher {

    private final Twitter twitter;
    private final long list;

    public ListTweetsFetcher(Twitter twitter, long list) {
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