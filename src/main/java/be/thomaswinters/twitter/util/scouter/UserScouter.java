package be.thomaswinters.twitter.util.scouter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UserScouter implements ITweetScouter {

    private final String user;
    private final boolean allowRetweets;
    private final boolean allowReplies;

    public UserScouter(String user, boolean allowRetweets, boolean allowReplies) {
        this.user = user;
        this.allowRetweets = allowRetweets;
        this.allowReplies = allowReplies;
    }

    public UserScouter(String user) {
        this(user, false, false);
    }

    public UserScouter() throws IllegalStateException, TwitterException {
        this(getOwnUsername());
    }

    public static String getOwnUsername() throws IllegalStateException, TwitterException {
        return new TwitterFactory().getInstance().getScreenName();
    }

    @Override
    public Collection<Status> scout(long sinceId) throws TwitterException {
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
        return statuses.stream().filter(e -> allowReplies || e.getInReplyToStatusId() <= 0)
                .filter(e -> allowRetweets || !e.isRetweet()).collect(Collectors.toList());
    }

}
