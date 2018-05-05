package be.thomaswinters.twitter.tweetsfetcher;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimelineTweetsFetcher extends AbstractPagingTweetsFetcher {
    public TimelineTweetsFetcher(Twitter twitter) {
        super(twitter);
    }

    @Override
    protected List<Status> getTweetsFromPage(Paging page) {
        try {
            return getTwitter().getHomeTimeline(page);
        } catch (TwitterException e) {
            if (e.exceededRateLimitation()) {
                try {
                    System.out.println("Exceeded Twitter rate: sleeping for 15 minutes!");
                    TimeUnit.MINUTES.sleep(15);
                    return getTwitter().getHomeTimeline(page);
                } catch (InterruptedException | TwitterException e1) {
                    throw new RuntimeException(e1);
                }
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
