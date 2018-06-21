package be.thomaswinters.twitter.bot.chatbot.data;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.chatbot.data.IChatUser;
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
    public String getText() {
        return tweet.getText();
    }

    @Override
    public Optional<IChatMessage> getPrevious() {
        if (tweet.getInReplyToStatusId() <= 1L) {
            return Optional.empty();
        }
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

    @Override
    public Optional<String> getURL() {
        return Optional.of("https://twitter.com/"+getUser().getScreenName()+"/status/"+getId());
    }

//    @Override
    public long getId() {
        return tweet.getId();
    }

    public Status getTweet() {
        return tweet;
    }

    @Override
    public String toString() {
        return getUser().getScreenName() + ": " + getText();
    }
}
