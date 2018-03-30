package be.thomaswinters.twitter;

import be.thomaswinters.bot.IChatBot;
import be.thomaswinters.bot.ITextGeneratorBot;
import be.thomaswinters.bot.bots.TextGeneratorChatBotAdaptor;
import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.chatbot.ITwitterChatBot;
import be.thomaswinters.twitter.bot.chatbot.TwitterChatBotAdaptor;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;

public class GeneratorTwitterBot extends TwitterBot {
    private final ITextGeneratorBot textGeneratorBot;
    private final ITwitterChatBot twitterChatBot;

    public GeneratorTwitterBot(Twitter twitterConnection, ITextGeneratorBot textGeneratorBot, ITwitterChatBot twitterChatBot) {
        super(twitterConnection);
        this.textGeneratorBot = textGeneratorBot;
        this.twitterChatBot = twitterChatBot;
    }

    public GeneratorTwitterBot(Twitter twitterConnection, ITextGeneratorBot textGeneratorBot, IChatBot chatBot) {
        this(twitterConnection, textGeneratorBot, new TwitterChatBotAdaptor(twitterConnection, chatBot));
    }

    public GeneratorTwitterBot(Twitter twitterConnection, ITextGeneratorBot textGeneratorBot) {
        this(twitterConnection, textGeneratorBot, new TwitterChatBotAdaptor(twitterConnection, new TextGeneratorChatBotAdaptor(textGeneratorBot)));
    }


    @Override
    public Optional<String> createReplyTo(Status mentionTweet) {
        return twitterChatBot.generateReply(mentionTweet);
    }

    @Override
    public Optional<String> prepareNewTweet() {
        return textGeneratorBot.generateText();
    }
}
