package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.arguments.PostingMode;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
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

    public void run(TwitterBotArguments arguments) throws TwitterException {

        if (arguments.getPostingMode().equals(PostingMode.POST)) {
            bot.postNewTweet();
        } else if (arguments.getPostingMode().equals(PostingMode.REPLY)) {
            bot.replyToAllUnrepliedMentions();
        }
    }

    public void run(String[] args) throws TwitterException {
        TwitterBotArguments arguments = argumentsCreator.get();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        run(arguments);
    }
}
