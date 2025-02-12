package be.thomaswinters.twitter.bot.tweeter;

import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import com.google.common.collect.ImmutableList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.List;
import java.util.Objects;

public class CompositeTweeter implements ITweeter {
    private final List<ITweeter> tweeters;

    public CompositeTweeter(List<ITweeter> tweeters) {
        this.tweeters = ImmutableList.copyOf(tweeters);
    }

    @Override
    public Status quoteRetweet(String status, Status toTweet) {
        return this.tweeters
                .stream()
                .map(t -> {
                    try {
                        return t.quoteRetweet(status, toTweet);
                    } catch (TwitterException e) {
                        throw new UncheckedTwitterException(e);
                    }
                })
                .reduce(null, (e, f) -> e == null ? f : e);
    }

    @Override
    public Status tweet(String status) {
        return this.tweeters
                .stream()
                .map(t -> {
                    try {
                        return t.tweet(status);
                    } catch (TwitterException e) {
                        throw new UncheckedTwitterException(e);
                    }
                })
                .reduce(null, (e, f) -> e == null ? f : e);
    }

    @Override
    public Status reply(String replyText, Status toTweet) {
        return this.tweeters
                .stream()
                .map(t -> {
                    try {
                        return t.reply(replyText, toTweet);
                    } catch (TwitterException e) {
                        throw new UncheckedTwitterException(e);
                    }
                })
                .reduce(null, (e, f) -> e == null ? f : e);
    }

    @Override
    public void follow(User user) {
        this.tweeters.forEach(t -> {
            try {
                t.follow(user);
            } catch (TwitterException e) {
                throw new UncheckedTwitterException(e);
            }
        });

    }

    @Override
    public void like(Status tweet) {
        this.tweeters.forEach(t -> {
            try {
                t.like(tweet);
            } catch (TwitterException e) {
                throw new UncheckedTwitterException(e);
            }
        });

    }

    @Override
    public Twitter getTwitterConnection() {
        return tweeters.stream().map(ITweeter::getTwitterConnection).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
