package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;

import java.util.Optional;

public class TweetingMode implements ITwitterBotMode {
    @Override
    public void execute(TwitterBot bot, TwitterBotArguments arguments) {
        if (arguments.isDebug()) {
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

        } else {

            bot.postNewTweet();
        }
    }
}
