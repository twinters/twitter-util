package be.thomaswinters.twitter.util.analysis;

import be.thomaswinters.twitter.tweetsfetcher.UserTweetsFetcher;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

public class TwitterAnalysisUtil {


    /**
     * Returns most recent tweet, excluding replies and retweets
     *
     * @return
     * @throws TwitterException
     */
    public static long getLastRealTweet(Twitter twitter) throws TwitterException {
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream()
                .filter(
                        e -> !e.getText().startsWith("@")
                                && !e.getText().startsWith("RT : "))
                .filter(e->e.getInReplyToStatusId() <= 0L )
                .mapToLong(Status::getId)
                .max()
                .orElse(0l);
    }

    public static Optional<Long> getOptionalLastReply(Twitter twitter, String username) throws TwitterException {
        ResponseList<Status> timeline = twitter.getUserTimeline(username);
        return new UserTweetsFetcher(twitter, username, false, true)
                .retrieve()
                .dropWhile(e -> e.getInReplyToStatusId() <= 0L)
                .findFirst()
                .map(Status::getId);
    }

    @Deprecated
    public static long getLastReply(Twitter twitter, String username) throws TwitterException {
        return getOptionalLastReply(twitter, username).orElse(1L);
    }

    public static Optional<Long> getOptionalLastReply(Twitter twitter) throws TwitterException {
        return getOptionalLastReply(twitter, twitter.getScreenName());
    }

    @Deprecated
    public static long getLastReply(Twitter twitter) throws TwitterException {
        return getLastReply(twitter, twitter.getScreenName());
    }

    public static Optional<Status> getLastReplyStatus(Twitter twitter) throws TwitterException {
        Optional<Long> id = getOptionalLastReply(twitter);
        if (id.isPresent()) {
            return Optional.ofNullable(twitter.showStatus(id.get()));
        }
        return Optional.empty();
    }
}
