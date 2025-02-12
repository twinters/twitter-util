package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.util.paging.PagingTweetFetcher;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.stream.Stream;

public class UserTweetsFetcher implements ITweetsFetcher {

    private final Twitter twitter;
    private final String user;
    private final boolean allowRetweets;
    private final boolean allowReplies;

    public UserTweetsFetcher(Twitter twitter, String user, boolean allowRetweets, boolean allowReplies) {
        this.twitter = twitter;
        this.user = user;
        this.allowRetweets = allowRetweets;
        this.allowReplies = allowReplies;
    }

    public UserTweetsFetcher(Twitter twitter, String user) {
        this(twitter, user, false, false);
    }


    @Override
    public Stream<Status> retrieve(long sinceId) {

        PagingTweetFetcher tweetDownloader = new PagingTweetFetcher(this::getUserTimeLine);
        return tweetDownloader.getTweets(sinceId)
                .filter(e -> allowReplies || e.getInReplyToStatusId() <= 0)
                .filter(e -> allowRetweets || !e.isRetweet());
    }

    private List<Status> getUserTimeLine(Paging page) {
        try {
            return twitter.getUserTimeline(user, page);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }
}
