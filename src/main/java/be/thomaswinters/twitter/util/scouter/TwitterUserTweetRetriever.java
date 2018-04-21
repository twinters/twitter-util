package be.thomaswinters.twitter.util.scouter;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TwitterUserTweetRetriever implements ITweetRetriever {

    private final String user;
    private final boolean allowRetweets;
    private final boolean allowReplies;

    public TwitterUserTweetRetriever(String user, boolean allowRetweets, boolean allowReplies) {
        this.user = user;
        this.allowRetweets = allowRetweets;
        this.allowReplies = allowReplies;
    }

    public TwitterUserTweetRetriever(String user) {
        this(user, false, false);
    }

    public TwitterUserTweetRetriever() throws IllegalStateException, TwitterException {
        this(getOwnUsername());
    }

    public static String getOwnUsername() throws IllegalStateException, TwitterException {
        return new TwitterFactory().getInstance().getScreenName();
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {
        Twitter twitter = new TwitterFactory().getInstance();

        int pageno = 1;
        List<Status> statuses = new ArrayList<>();

        while (true) {
            try {
                int size = statuses.size();
                Paging page = new Paging(pageno++, 100);
                statuses.addAll(twitter.getUserTimeline(user, page));
                if (statuses.size() == size)
                    break;
            } catch (TwitterException e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("Total: " + statuses.size());
        return statuses.stream()
                .filter(e -> allowReplies || e.getInReplyToStatusId() <= 0)
                .filter(e -> allowRetweets || !e.isRetweet());
    }

}
