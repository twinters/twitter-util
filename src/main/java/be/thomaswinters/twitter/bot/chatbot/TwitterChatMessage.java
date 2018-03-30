package be.thomaswinters.twitter.bot.chatbot;

import be.thomaswinters.bot.data.IChatMessage;
import be.thomaswinters.bot.data.IChatUser;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

public class TwitterChatMessage implements IChatMessage {
    private final Twitter twitter;
    private final Status tweet;

    public TwitterChatMessage(Twitter twitter, Status tweet) {
        this.twitter = twitter;
        this.tweet = tweet;
    }

    @Override
    public String getMessage() {
        return tweet.getText();
    }

    @Override
    public Optional<IChatMessage> getPrevious() {
        try {
            Status previousTweet = twitter.showStatus(tweet.getInReplyToStatusId());
            return Optional.of(new TwitterChatMessage(twitter, previousTweet));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public IChatUser getUser() {
        return new TwitterChatUser(tweet.getUser());
    }

}
