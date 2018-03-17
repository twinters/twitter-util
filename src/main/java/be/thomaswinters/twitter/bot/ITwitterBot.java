package be.thomaswinters.twitter.bot;

import twitter4j.*;

import java.util.Optional;

public interface ITwitterBot {
    Optional<Status> execute(long sinceId) throws TwitterException;

    default Optional<Status> execute() throws TwitterException {

        // Use as execute
        return execute(getLastRealTweet());
    }

    default long getLastTweet() throws TwitterException {
        Twitter twitter = getTwitterConnection();
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream().mapToLong(e -> e.getId()).max().orElse(0l);
    }


    /**
     * Returns most recent tweet, excluding replies and retweets
     *
     * @return
     * @throws TwitterException
     */
    default long getLastRealTweet() throws TwitterException {
        Twitter twitter = getTwitterConnection();
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream().filter(e -> !e.getText().startsWith("@") && !e.getText().startsWith("RT : ")).mapToLong(e -> e.getId()).max().orElse(0l);
    }

    default Twitter getTwitterConnection() {
        return TwitterFactory.getSingleton();
    }

    default boolean isValidTweet(String text) {
        return text.length() <= 140;
    }
}
