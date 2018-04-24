package be.thomaswinters.twitter;

import be.thomaswinters.chatbot.IChatBot;
import be.thomaswinters.chatbot.bots.TextGeneratorChatBotAdaptor;
import be.thomaswinters.generator.generators.FilteringGenerator;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.chatbot.ITwitterChatBot;
import be.thomaswinters.twitter.bot.chatbot.TwitterChatBotAdaptor;
import be.thomaswinters.twitter.util.IExtractableChatBot;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;

public class GeneratorTwitterBot extends TwitterBot implements IExtractableChatBot {
    private final IGenerator<String> textGeneratorBot;
    private final ITwitterChatBot twitterChatBot;

    public GeneratorTwitterBot(Twitter twitterConnection, IGenerator<String> textGeneratorBot, ITwitterChatBot twitterChatBot) {
        super(twitterConnection);
        this.textGeneratorBot = new FilteringGenerator<>(textGeneratorBot, TwitterUtil::hasValidLength);
        this.twitterChatBot = twitterChatBot;
    }

    public GeneratorTwitterBot(Twitter twitterConnection, IGenerator<String> textGeneratorBot, IChatBot chatBot) {
        this(twitterConnection, textGeneratorBot, new TwitterChatBotAdaptor(twitterConnection, chatBot));
    }

    public GeneratorTwitterBot(Twitter twitterConnection, IGenerator<String> textGeneratorBot) {
        this(twitterConnection, textGeneratorBot, new TwitterChatBotAdaptor(twitterConnection, new TextGeneratorChatBotAdaptor(textGeneratorBot)));
    }


    @Override
    public Optional<String> createReplyTo(Status mentionTweet) {
        return twitterChatBot.generateReply(mentionTweet);
    }

    @Override
    public Optional<String> prepareNewTweet() {
        return textGeneratorBot.generate();
    }

    @Override
    public Optional<IChatBot> getChatBot() {
        if (twitterChatBot instanceof IExtractableChatBot) {
            return ((IExtractableChatBot) twitterChatBot).getChatBot();
        }
        return Optional.empty();
    }
}
