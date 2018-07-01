package be.thomaswinters.twitter.bot.util;

import be.thomaswinters.twitter.bot.tweeter.Tweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;
import java.util.function.Supplier;

public class LastRepliedToSupplier implements Supplier<Long> {

    private final Twitter twitter;
    private Status lastRepliedTo;

    public LastRepliedToSupplier(Twitter twitter) {
        this.twitter = twitter;
    }

    public void updateLastStatus(Status recentTweet) {
        if (lastRepliedTo == null
                || recentTweet.getInReplyToStatusId() > lastRepliedTo.getInReplyToStatusId()) {
            lastRepliedTo = recentTweet;
        }
    }

    public void subscribeToTweeter(Tweeter tweeter) {
        tweeter.addReplyListener((tweeted, to) -> updateLastStatus(tweeted));
    }

    @Override
    public Long get() {
        return Long.max(Optional.ofNullable(lastRepliedTo).map(Status::getInReplyToStatusId).orElse(1L),
                TwitterUnchecker.uncheck(TwitterUtil::getLastReplyStatus, twitter).map(Status::getInReplyToStatusId)
                        .orElse(1L));
    }
}
