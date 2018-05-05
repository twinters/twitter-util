package be.thomaswinters.twitter.util;

import twitter4j.*;

import java.util.concurrent.TimeUnit;

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
        return timeline.stream()
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

    private static final String TWITTER_USERNAME_REGEX = "(?<=^|(?<=[^a-zA-Z0-9-\\.]))@[A-Za-z0-9-]+(?=[^a-zA-Z0-9-_\\.])";
    private static final String TWITTER_HASHTAG_REGEX = "(?:\\s|\\A)[##]+([A-Za-z0-9-_]+)";


    public static String removeTwitterWords(String text) {
        return text.replaceAll(TWITTER_USERNAME_REGEX, "")
                .replaceAll(TWITTER_HASHTAG_REGEX, "");
    }

    public static void waitForExceededRateLimitationReset() {
        try {
            TimeUnit.MINUTES.sleep(15);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
