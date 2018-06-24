package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import twitter4j.TwitterException;

public interface ITwitterBotMode {

    void execute(TwitterBot bot, TwitterBotArguments arguments) throws TwitterException;
}
