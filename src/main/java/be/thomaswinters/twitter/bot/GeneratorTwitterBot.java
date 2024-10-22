package be.thomaswinters.twitter.bot;

import be.thomaswinters.chatbot.IChatBot;
import be.thomaswinters.chatbot.bots.TextGeneratorChatBotAdaptor;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.chatbot.TwitterChatBotAdaptor;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.util.IExtractableChatBot;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;

@Deprecated
public class GeneratorTwitterBot extends TwitterBot implements IExtractableChatBot {
    //    private final IGenerator<String> textGeneratorBot;
    private final IReactingGenerator<String, Status> twitterChatBot;

    public GeneratorTwitterBot(Twitter twitterConnection,
                               IGenerator<String> textGeneratorBot,
                               IReactingGenerator<String, Status> twitterChatBot,
                               ITweetsFetcher retriever) {
        super(twitterConnection,
                BehaviourCreator.fromTextGenerator(textGeneratorBot.filter(1, TwitterUtil::hasValidLength)),
                BehaviourCreator.fromStatusReactor(twitterChatBot),
                retriever);
        this.twitterChatBot = twitterChatBot;
    }

    public GeneratorTwitterBot(Twitter twitterConnection,
                               IGenerator<String> textGeneratorBot,
                               IChatBot chatBot,
                               ITweetsFetcher retriever) {
        this(twitterConnection, textGeneratorBot, new TwitterChatBotAdaptor(twitterConnection, chatBot), retriever);
    }

    public GeneratorTwitterBot(Twitter twitterConnection, IGenerator<String> textGeneratorBot) {
        this(twitterConnection, textGeneratorBot,
                new TwitterChatBotAdaptor(twitterConnection,
                        new TextGeneratorChatBotAdaptor(textGeneratorBot)), MENTIONS_RETRIEVER.apply(twitterConnection));
    }


    @Override
    public Optional<IChatBot> getChatBot() {
        if (twitterChatBot instanceof IExtractableChatBot) {
            return ((IExtractableChatBot) twitterChatBot).getChatBot();
        }
        return Optional.empty();
    }
}
