package be.thomaswinters.twitter.bot.chatbot;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

public class LikingTwitterBot implements ITwitterChatBot {
    private final Twitter twitter;

    public LikingTwitterBot(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Optional<String> generateReply(Status tweet) {
        if (!tweet.isFavorited()) {
            try {
                twitter.createFavorite(tweet.getId());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
