package be.thomaswinters.twitter.util;

import twitter4j.Status;

public class TwitterUtil {
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
}
