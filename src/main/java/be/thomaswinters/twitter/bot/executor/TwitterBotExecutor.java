package be.thomaswinters.twitter.bot.executor;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.loggers.TweetPrinter;
import be.thomaswinters.twitter.bot.loggers.TweetReplyPrinter;
import be.thomaswinters.twitter.bot.loggers.TwitterFollowPrinter;
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
        TweetPrinter tweetPrinter = new TweetPrinter();
        TweetReplyPrinter tweetReplyPrinter = new TweetReplyPrinter();
        TwitterFollowPrinter twitterFollowPrinter = new TwitterFollowPrinter(bot.getTwitterConnection().getScreenName());
        if (arguments.isLogging()) {
            bot.getTweeter().addPostListener(tweetPrinter);
            bot.getTweeter().addReplyListener(tweetReplyPrinter);
            bot.getTweeter().addFollowListener(twitterFollowPrinter);
        }

        for (int i = 0; arguments.isInfinity() || i < arguments.getPostTimes(); i++) {
            arguments.getMode().execute(bot, arguments);
        }

        if (arguments.isLogging()) {
            bot.getTweeter().removePostListener(tweetPrinter);
            bot.getTweeter().removeReplyListener(tweetReplyPrinter);
            bot.getTweeter().removeFollowListener(twitterFollowPrinter);
        }
    }


    public void run(String[] args) throws TwitterException {
        TwitterBotArguments arguments = argumentsCreator.get();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        run(arguments);
    }
}
