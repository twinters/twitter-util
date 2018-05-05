package be.thomaswinters.twitter.util.analysis;

import be.thomaswinters.twitter.util.retriever.TwitterUserTweetRetriever;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

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
                .mapToLong(Status::getId)
                .max()
                .orElse(0l);
    }

    public static long getLastReply(Twitter twitter, String username) throws TwitterException {
        ResponseList<Status> timeline = twitter.getUserTimeline(username);
        return new TwitterUserTweetRetriever(twitter, username, false, true)
                .retrieve()
                .dropWhile(e -> e.getInReplyToStatusId() <= 0l)
                .findFirst()
                .map(Status::getId)
                .orElse(1l);
    }

    public static long getLastReply(Twitter twitter) throws TwitterException {
        return getLastReply(twitter, twitter.getScreenName());
    }

    public static Status getLastReplyStatus(Twitter twitter) throws TwitterException {
        long id = getLastReply(twitter);
        return twitter.showStatus(id);
    }
}
