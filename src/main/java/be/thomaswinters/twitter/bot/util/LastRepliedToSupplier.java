package be.thomaswinters.twitter.bot.util;

import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.function.Supplier;

public class LastRepliedToSupplier implements Supplier<Long> {

    private final Twitter twitter;
    private long lastRepliedTo;

    public LastRepliedToSupplier(Twitter twitter) {
        this.twitter = twitter;
        this.lastRepliedTo = getLastRepliedToStatus(twitter);
    }

    public static long getLastRepliedToStatus(Twitter twitter) {
        return TwitterUnchecker
                .uncheck(TwitterUtil::getLastReplyStatus, twitter)
                .map(Status::getInReplyToStatusId)
                .orElse(1L);
    }

    public void updateLastInspectedTweetToAnswer(Status lastInspected) {
        if (lastRepliedTo < lastInspected.getId()) {
            lastRepliedTo = lastInspected.getId();
        }
    }

    public void subscribeToTweeter(TwitterBot twitterBot) {
        twitterBot.addInspectedTweetToAnswerListener(this::updateLastInspectedTweetToAnswer);
    }

    @Override
    public Long get() {
        return Long.max(lastRepliedTo, getLastRepliedToStatus(twitter));
    }
}
