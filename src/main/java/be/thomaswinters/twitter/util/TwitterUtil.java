package be.thomaswinters.twitter.util;

import twitter4j.*;

import java.util.concurrent.TimeUnit;

public class TwitterUtil {


    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TWITTER_USERNAME_REGEX = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@[A-Za-z0-9-_]+(?=[^a-zA-Z0-9-_\\.])";
    public static final String TWITTER_HASHTAG_REGEX = "(?:\\s|\\A)[##]+([A-Za-z0-9-_]+)";

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

    public static boolean isFuzzyReplyingTo(String userName, Status mention) throws TwitterException {
        String name = "@" + userName.toLowerCase();

        String tweetText = mention.getText().toLowerCase();

        while (tweetText.startsWith("@")) {
            if (tweetText.startsWith(name)) {
                // If the only content left is the tag, then it's not a reply but a tag.
                return !tweetText.trim().equals(name);
            }
            if (tweetText.contains(" ")) {
                tweetText = tweetText.split(" ", 2)[1];
            } else {
                tweetText = "";
            }
        }

        return false;
    }

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

    public static Status getParentTweetIfJustMentioning(Twitter twitter, Status mentionTweet) {
        Status tweetToGetTextFrom = mentionTweet;
        try {
            if (!isFuzzyReplyingTo(twitter.getScreenName(), tweetToGetTextFrom)
                    && mentionsUser(twitter.getScreenName(), tweetToGetTextFrom)
                    && mentionTweet.getInReplyToStatusId() > 0) {
                tweetToGetTextFrom = twitter.showStatus(mentionTweet.getInReplyToStatusId());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return tweetToGetTextFrom;
    }

    private static boolean mentionsUser(String screenName, Status tweet) {
        return tweet.getText().toLowerCase().contains("@" + screenName.toLowerCase());
    }
}
