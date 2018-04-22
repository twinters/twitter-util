package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
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
        this(bot, () -> new TwitterBotArguments());
    }

    public void run(TwitterBotArguments arguments) throws TwitterException {

        for (int i = 0; arguments.isInfinity() || i < arguments.getAmountOfTimes(); i++) {

            if (arguments.isDebug()) {
                if (arguments.isPosting()) {

                    Optional<String> tweet = bot.prepareNewTweet();
                    if (tweet.isPresent()) {
                        System.out.println(tweet.get());
                    } else {
                        System.out.println("Failed to prepare new tweet");
                    }
                    System.out.println("\n\n");
                }
                if (arguments.isReplying()) {
                    throw new RuntimeException("Debugging replies not supported yet");
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
