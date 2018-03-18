package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.arguments.PostingMode;
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

        for (int i = 0; i < arguments.getAmountOfTimes(); i++) {

            if (arguments.isDebug()) {
                if (arguments.getPostingMode().equals(PostingMode.POST)) {

                    Optional<String> tweet = bot.prepareNewTweet();
                    if (tweet.isPresent()) {
                        System.out.println(tweet.get());
                    } else {
                        System.out.println("Failed to prepare new tweet");
                    }
                    System.out.println("\n\n");
                } else if (arguments.getPostingMode().equals(PostingMode.REPLY)) {
                    throw new RuntimeException("Debugging replies not supported yet");
                }

            } else {
                if (arguments.getPostingMode().equals(PostingMode.POST)) {
                    bot.postNewTweet();
                } else if (arguments.getPostingMode().equals(PostingMode.REPLY)) {
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
