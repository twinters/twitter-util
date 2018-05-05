package be.thomaswinters.twitter.tweetsfetcher;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class ListTweetsFetcher extends AbstractPagingTweetsFetcher {

    private final long list;

    public ListTweetsFetcher(Twitter twitter, long list) {
        super(twitter);
        this.list = list;
    }

    @Override
    protected List<Status> getTweetsFromPage(Paging page) {
        try {
            return getTwitter().getUserListStatuses(list, page);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

}
