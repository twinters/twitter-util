package be.thomaswinters.twitter.bot.executor;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.executor.modes.ReplyingMode;
import be.thomaswinters.twitter.bot.executor.modes.TweetingMode;
import be.thomaswinters.twitter.bot.loggers.TweetPrinter;
import be.thomaswinters.twitter.bot.loggers.TweetReplyPrinter;
import com.beust.jcommander.JCommander;
import twitter4j.TwitterException;

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
            if (arguments.isPosting()) {
                new TweetingMode().execute(bot, arguments);
            }
            if (arguments.isReplying()) {
                new ReplyingMode().execute(bot, arguments);
            }
        }
    }


    public void run(String[] args) throws TwitterException {
        TwitterBotArguments arguments = argumentsCreator.get();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        run(arguments);
    }
}
