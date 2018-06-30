package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.TwitterException;

import java.util.List;

public class CompositeMode implements ITwitterBotMode {
    private final List<ITwitterBotMode> modes;

    public CompositeMode(List<ITwitterBotMode> modes) {
        this.modes = modes;
    }

    @Override
    public void execute(TwitterBot bot, ITweeter tweeter, TwitterBotArguments arguments) throws TwitterException {
        modes.parallelStream().forEach(mode -> {
            try {
                mode.execute(bot, tweeter, arguments);
            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
        });
    }
}
