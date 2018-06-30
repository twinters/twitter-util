package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class ReplyMode implements ITwitterBotMode {
    @Override
    public void execute(TwitterBot bot, ITweeter tweeter, TwitterBotArguments arguments) {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plus(arguments.getRunDuration());

        while (LocalDateTime.now().isBefore(end)) {
            LocalDateTime startOfRun = LocalDateTime.now();

            // Reply to all
            try {
                bot.replyToAllUnrepliedMentions(tweeter);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            // Wait for a while
            long waitForseconds = arguments.getPostMinimumWait().get(ChronoUnit.SECONDS)
                    - ChronoUnit.SECONDS.between(startOfRun, LocalDateTime.now());
            try {
                TimeUnit.SECONDS.sleep(waitForseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
