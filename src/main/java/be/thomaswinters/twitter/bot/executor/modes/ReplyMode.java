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
        System.out.println("Started replying mode until " + end);

        do {
            LocalDateTime startOfRun = LocalDateTime.now();

            // Reply to all
            try {
                bot.replyToAllUnrepliedMentions(tweeter);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            // Wait for a while
            long waitForSeconds = arguments.getReplyWait().get(ChronoUnit.SECONDS)
                    - ChronoUnit.SECONDS.between(startOfRun, LocalDateTime.now());
            try {
                System.out.println("Sleeping for seconds after reply check: " + waitForSeconds + " " + this);
                TimeUnit.SECONDS.sleep(waitForSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (LocalDateTime.now().isBefore(end));

    }
}
