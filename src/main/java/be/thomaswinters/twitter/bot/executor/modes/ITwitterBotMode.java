package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.TwitterException;

public interface ITwitterBotMode {

    void execute(TwitterBot bot, ITweeter tweeter, TwitterBotArguments arguments) throws TwitterException;
}
