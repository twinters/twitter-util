package be.thomaswinters.twitter.bot.util;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.TwitterException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Deprecated
public class InfinityTweeter {
    private final TwitterBot bot;
    private final int minutesOfPostingSleep;
    private final int minutesOfReplyingSleep;
    private boolean isTerminated = false;

    public InfinityTweeter(TwitterBot bot,
                           int minutesOfPostingSleep, int minutesOfReplyingSleep) {
        if (bot == null || minutesOfPostingSleep == 0 || minutesOfReplyingSleep == 0) {
            throw new IllegalArgumentException("Null values in infinity tweeter");
        }

        this.bot = bot;
        this.minutesOfPostingSleep = minutesOfPostingSleep;
        this.minutesOfReplyingSleep = minutesOfReplyingSleep;

    }

    public InfinityTweeter(TwitterBot bot,
                           int minutesOfPostingSleep) {
        this(bot, minutesOfPostingSleep, 5);
    }


    public void terminate() {
        this.isTerminated = true;
    }

    public void tweetForever() throws InterruptedException, TwitterException {
        // keep tweeting forever
        while (!isTerminated) {

            LocalDateTime nextPostDate = LocalDateTime.now().plusMinutes(minutesOfPostingSleep);

            // TWEET
            System.out.println("Preparing new tweet...");
//            Optional<Status> status = Optional.empty();
            try {
                bot.postNewTweet();
            } catch (Exception e) {
                System.out.println("ERROR:" + e.getMessage());
            }

            // Check for status
//            status.ifPresent(status1 -> System.out.println("\nI posted a tweet! --> " + status1.getText()));


            System.out.println("Listening for replies...");

            // Check for new messages every minute
            while (nextPostDate.isAfter(LocalDateTime.now().plusMinutes(minutesOfReplyingSleep))) {
                LocalDateTime nextReplyDate = LocalDateTime.now().plusMinutes(minutesOfReplyingSleep);
                bot.replyToAllUnrepliedMentions();

                long timeToNextReply = ChronoUnit.SECONDS.between(LocalDateTime.now(), nextReplyDate);
                if (timeToNextReply > 0) {
                    Thread.sleep(timeToNextReply * 1000);
                }
            }

            Thread.sleep(ChronoUnit.SECONDS.between(LocalDateTime.now(), nextPostDate) * 1000);

        }

    }

}
