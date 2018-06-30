package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PostMode implements ITwitterBotMode {
    @Override
    public void execute(TwitterBot bot, ITweeter tweeter, TwitterBotArguments arguments) {

        LocalDateTime start = LocalDateTime.now();

        List<LocalDateTime> postMoments = createPostMoments(start,
                arguments.getRunDuration(),
                arguments.getPostTimes());


        for (LocalDateTime postMoment : postMoments) {

            // Sleep until next one
            long sleepDuration =
                    ChronoUnit.SECONDS.between(LocalDateTime.now(), postMoment);

            try {
                System.out.println("Sleeping for " + sleepDuration + " seconds until next tweet");
                TimeUnit.SECONDS.sleep(sleepDuration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            bot.postNewTweet(tweeter);


        }
    }

    private List<LocalDateTime> createPostMoments(LocalDateTime start, TemporalAmount runDuration, int postTimes) {
        long amountOfSeconds = runDuration.get(ChronoUnit.SECONDS);

        return IntStream
                .range(1, postTimes)
                .mapToObj(i -> {
                            if (amountOfSeconds > 0) {
                                return start.plusSeconds(ThreadLocalRandom.current().nextLong(amountOfSeconds));
                            } else {
                                return start;
                            }
                        }
                )
                .sorted()
                .collect(Collectors.toList());
    }
}
