package be.thomaswinters.twitter.bot;

import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class SelectivelyReplyingTwitterBot extends TwitterBot {
    public static final Predicate<Status> NOT_YET_LIKED = status -> !status.isFavorited();

    private final TwitterBot innerTwitterBot;
    private final Predicate<Status> mentionFilter;


    @SafeVarargs
    public SelectivelyReplyingTwitterBot(Twitter twitterConnection, TwitterBot innerTwitterBot, Predicate<Status>... mentionFilters) {
        super(twitterConnection);
        this.innerTwitterBot = innerTwitterBot;
        this.mentionFilter = Arrays.stream(mentionFilters).reduce(x -> true, Predicate::and);
    }

    public SelectivelyReplyingTwitterBot(Twitter twitterConnection, TwitterBot innerTwitterBot, Predicate<Status> mentionFilter) {
        super(twitterConnection);
        this.innerTwitterBot = innerTwitterBot;
        this.mentionFilter = mentionFilter;
    }


    @Override
    public Optional<String> createReplyTo(Status mentionTweet) {
        if (mentionFilter.test(mentionTweet)) {
            return innerTwitterBot.createReplyTo(mentionTweet);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> prepareNewTweet() {
        return innerTwitterBot.prepareNewTweet();
    }
}
