package be.thomaswinters.twitter.util;

import be.thomaswinters.twitter.bot.IReplyingTwitterBot;
import be.thomaswinters.twitter.bot.ITwitterBot;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.OptionalLong;

public class InfinityTweeter {
    private final Optional<ITwitterBot> postingBot;
    private final Optional<IReplyingTwitterBot> replyingBot;
    private final int minutesOfPostingSleep;
    private final int minutesOfReplyingSleep;
    private boolean isTerminated = false;

    public InfinityTweeter(Optional<? extends ITwitterBot> postBot, Optional<? extends IReplyingTwitterBot> replyBot,
                           int minutesOfPostingSleep, int minutesOfReplyingSleep) {
        if (postBot == null || replyBot == null || minutesOfPostingSleep == 0 || minutesOfReplyingSleep == 0) {
            throw new IllegalArgumentException("Null values in infinity tweeter");
        }

        this.postingBot = postBot.map(e -> e);
        this.replyingBot = replyBot.map(e -> e);
        this.minutesOfPostingSleep = minutesOfPostingSleep;
        this.minutesOfReplyingSleep = minutesOfReplyingSleep;

    }

    public InfinityTweeter(Optional<? extends ITwitterBot> bot, Optional<? extends IReplyingTwitterBot> replyBot,
                           int minutesOfPostingSleep) {
        this(bot, replyBot, minutesOfPostingSleep, 5);
    }

    public InfinityTweeter(Optional<? extends ITwitterBot> bot, IReplyingTwitterBot replyBot, int minutesOfSleep) {
        this(bot, Optional.of(replyBot), minutesOfSleep);
    }

    public InfinityTweeter(ITwitterBot bot, int minutesOfSleep) {
        this(Optional.of(bot), Optional.empty(), minutesOfSleep);
    }

    public void terminate() {
        this.isTerminated = true;
    }

    public void tweetForever() throws InterruptedException, TwitterException {
        long sinceId = 0;

        // keep tweeting forever
        while (!isTerminated) {

            LocalDateTime nextPostDate = LocalDateTime.now().plusMinutes(minutesOfPostingSleep);

            // TWEET
            if (postingBot.isPresent()) {
                System.out.println("Preparing new tweet...");
                Optional<Status> status = Optional.empty();
                try {
                    status = postingBot.get().execute(sinceId);
                    if (status.isPresent()) {
                        sinceId = Math.max(sinceId, status.get().getId());
                    }
                } catch (Exception e) {
                    System.out.println("ERROR:" + e.getMessage());
                }

                // Check for status
                if (status.isPresent()) {
                    System.out.println("\nI posted a tweet! --> " + status.get().getText());
                }
            }

            // REPLY TO EVERYTHING
            if (replyingBot.isPresent()) {

                System.out.println("Listening for replies...");

                // Check for new messages every minute
                while (!postingBot.isPresent()
                        || nextPostDate.isAfter(LocalDateTime.now().plusMinutes(minutesOfReplyingSleep))) {
                    LocalDateTime nextReplyDate = LocalDateTime.now().plusMinutes(minutesOfReplyingSleep);
                    replyingBot.get().replyToAllUnrepliedMentions();

                    long timeToNextReply = ChronoUnit.SECONDS.between(LocalDateTime.now(), nextReplyDate);
                    if (timeToNextReply > 0) {
                        Thread.sleep(timeToNextReply * 1000);
                    }
                }

            }
            Thread.sleep(ChronoUnit.SECONDS.between(LocalDateTime.now(), nextPostDate) * 1000);

        }
    }

    public static long findLastStatusUpdate(Twitter twitter) throws IllegalStateException, TwitterException {
        String user = twitter.getScreenName();

        ResponseList<Status> tweets = twitter.getUserTimeline(user);

        OptionalLong result = tweets.stream().mapToLong(e -> e.getId()).max();
        if (!result.isPresent()) {
            return 0;
        }
        return result.getAsLong();
    }

    public static long findLastReply(Twitter twitter) throws IllegalStateException, TwitterException {
        String user = twitter.getScreenName();

        ResponseList<Status> tweets = twitter.getUserTimeline(user);

        OptionalLong result = tweets.stream().filter(e -> e.getInReplyToStatusId() > 0).mapToLong(e -> e.getId()).max();
        if (!result.isPresent()) {
            return 0;
        }
        return result.getAsLong();
    }

}
