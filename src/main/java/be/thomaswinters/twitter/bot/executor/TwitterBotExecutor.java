package be.thomaswinters.twitter.bot.executor;

import be.thomaswinters.chatbot.IChatBot;
import be.thomaswinters.chatbot.ui.ChatbotGUI;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.loggers.TweetPrinter;
import be.thomaswinters.twitter.bot.loggers.TweetReplyPrinter;
import be.thomaswinters.twitter.util.IExtractableChatBot;
import com.beust.jcommander.JCommander;
import twitter4j.TwitterException;

import java.util.Optional;
import java.util.function.Supplier;

public class TwitterBotExecutor {

    private final TwitterBot bot;
    private final Supplier<? extends TwitterBotArguments> argumentsCreator;

    public TwitterBotExecutor(TwitterBot bot, Supplier<? extends TwitterBotArguments> argumentsCreator) {
        this.bot = bot;
        this.argumentsCreator = argumentsCreator;
    }

    public TwitterBotExecutor(TwitterBot bot) {
        this(bot, TwitterBotArguments::new);
    }

    public void run(TwitterBotArguments arguments) throws TwitterException {
        if (arguments.isLog()) {
            bot.addPostListener(new TweetPrinter());
            bot.addReplyListener(new TweetReplyPrinter());
        }

        for (int i = 0; arguments.isInfinity() || i < arguments.getAmountOfTimes(); i++) {

            if (arguments.isDebug()) {
                if (arguments.isPosting()) {
                    if (!(bot instanceof IGenerator)) {
                        throw new IllegalArgumentException("Can't debug a bot that is not a generator " + bot);
                    }
                    IGenerator<?> generator = (IGenerator<?>) bot;
                    Optional<?> tweet = generator.generate();
                    if (tweet.isPresent()) {
                        System.out.println(">> POSTED TWEET IN DEBUG: << " + tweet.get());
                    } else {
                        System.out.println("Failed to prepare new tweet");
                    }
                    System.out.println("\n\n");
                }
                if (arguments.isReplying()) {
                    if (bot instanceof IExtractableChatBot) {
                        Optional<IChatBot> botOptional = ((IExtractableChatBot) bot).getChatBot();
                        String botName = bot.getTwitterConnection().getScreenName();
                        botOptional.ifPresent(chatbot -> new ChatbotGUI(chatbot, botName).run());
                    } else {
                        throw new RuntimeException("Debugging replies not fully supported yet. " +
                                "Please implement IExtractableChatBot to debug replies");
                    }
                }

            } else {
                if (arguments.isPosting()) {
                    bot.postNewTweet();
                }
                if (arguments.isReplying()) {
                    bot.replyToAllUnrepliedMentions();
                }

            }
        }
    }


    public void run(String[] args) throws TwitterException {
        TwitterBotArguments arguments = argumentsCreator.get();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        run(arguments);
    }
}
