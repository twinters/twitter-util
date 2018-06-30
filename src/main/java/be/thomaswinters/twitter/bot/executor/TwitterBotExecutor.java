package be.thomaswinters.twitter.bot.executor;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.tweeter.CompositeTweeter;
import be.thomaswinters.twitter.bot.tweeter.DebugTweeter;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import com.beust.jcommander.JCommander;
import twitter4j.TwitterException;

import java.util.Arrays;
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

        arguments.getMode().execute(bot, getTweeter(arguments), arguments);

    }

    private ITweeter getTweeter(TwitterBotArguments arguments) {
        ITweeter tweeter;
        if (arguments.isDebug()) {
            tweeter = new DebugTweeter();
        } else if (arguments.isLogging()) {
            tweeter = createLoggingTweeter(bot.getTweeter());
        } else {
            tweeter = bot.getTweeter();
        }
        return tweeter;

    }

    private ITweeter createLoggingTweeter(ITweeter originalTweeter) {
        try {
            return new CompositeTweeter(Arrays.asList(originalTweeter,
                    new DebugTweeter("@" + originalTweeter.getTwitterConnection().getScreenName())));
        } catch (TwitterException e) {
            throw new UncheckedTwitterException(e);
        }
    }


    public void run(String[] args) throws TwitterException {
        TwitterBotArguments arguments = argumentsCreator.get();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        run(arguments);
    }
}
