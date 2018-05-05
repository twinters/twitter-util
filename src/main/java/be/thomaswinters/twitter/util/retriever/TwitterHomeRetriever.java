package be.thomaswinters.twitter.util.retriever;

import be.thomaswinters.twitter.util.retriever.util.PagingTweetDownloader;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TwitterHomeRetriever implements ITweetRetriever {

    private final Twitter twitter;

    public TwitterHomeRetriever(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        try {
            long ownId = twitter.getId();
            return new PagingTweetDownloader(this::getHomeTweets)
                    .getTweets(sinceId)
                    .filter(status -> status.getUser().getId() != ownId);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Status> getHomeTweets(Paging page) {
        try {
            return twitter.getHomeTimeline(page);
        } catch (TwitterException e) {
            if (e.exceededRateLimitation()) {
                try {
                    System.out.println("Exceeded Twitter rate: sleeping for 15 minutes!");
                    TimeUnit.MINUTES.sleep(15);
                    return twitter.getHomeTimeline(page);
                } catch (InterruptedException | TwitterException e1) {
                    throw new RuntimeException(e1);
                }
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
