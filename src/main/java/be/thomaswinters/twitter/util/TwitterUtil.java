package be.thomaswinters.twitter.util;

import twitter4j.*;

public class TwitterUtil {


    public static final int MAX_TWEET_LENGTH = 280;

    public static String getQuoteRetweetUrl(Status status) {
        return "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
    }

    /**
     * Calculates the original status that was retweeted
     *
     * @param status
     * @return
     */
    public static Status getOriginalStatus(Status status) {

        Status originalStatus = status;
        while (originalStatus.isRetweet()) {
            originalStatus = originalStatus.getRetweetedStatus();
        }

        return originalStatus;
    }

    public static boolean isTwitterWord(String word) {
        return word.startsWith("@") || word.startsWith("#") || word.startsWith("http://") || word.startsWith("https://");
    }


    public static long getLastTweet(Twitter twitter) throws TwitterException {
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream().mapToLong(e -> e.getId()).max().orElse(0l);
    }

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

    public static boolean hasValidLength(String text) {
        return text.length() <= MAX_TWEET_LENGTH;
    }

    public static boolean isDirectReplyToCurrentUser(Twitter twitterConnection, Status mentionTweet) throws TwitterException {
        return isDirectReplyToUser(twitterConnection.getId(), twitterConnection.getScreenName(), mentionTweet);
    }

    public static boolean isDirectReplyToUser(User user, Status mentionTweet) {
        return isDirectReplyToUser(user.getId(), user.getScreenName(), mentionTweet);

    }

    public static boolean isDirectReplyToUser(long userId, String screenName, Status mentionTweet) {
        return mentionTweet.getText().toLowerCase().startsWith("@" + screenName.toLowerCase())
                || mentionTweet.getInReplyToUserId() == userId;
    }
}
