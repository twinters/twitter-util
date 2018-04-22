package be.thomaswinters.twitter.bot.chatbot;

import be.thomaswinters.bot.IChatBot;
import be.thomaswinters.twitter.bot.chatbot.data.TwitterChatMessage;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;

/**
 * Adaptor to allow an IChatBot to function as a ITwitterChatBot
 */
public class TwitterChatBotAdaptor implements ITwitterChatBot {
    private final Twitter twitter;
    private final IChatBot chatBot;

    public TwitterChatBotAdaptor(Twitter twitter, IChatBot chatBot) {
        this.twitter = twitter;
        this.chatBot = chatBot;
    }

    @Override
    public Optional<String> generateReply(Status tweet) {
        return chatBot.generateReply(new TwitterChatMessage(twitter, tweet));
    }

}
