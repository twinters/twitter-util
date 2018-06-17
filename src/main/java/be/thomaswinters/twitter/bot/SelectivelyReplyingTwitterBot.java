package be.thomaswinters.twitter.bot;

import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Deprecated
public class SelectivelyReplyingTwitterBot extends TextualTwitterBot {
    public static final Predicate<Status> NOT_YET_LIKED = status -> !status.isFavorited();

    private final TextualTwitterBot innerTwitterBot;
    private final Predicate<Status> mentionFilter;


    @SafeVarargs
    public SelectivelyReplyingTwitterBot(Twitter twitterConnection, TextualTwitterBot innerTwitterBot, Predicate<Status>... mentionFilters) {
        super(twitterConnection, MENTIONS_RETRIEVER.apply(twitterConnection));
        this.innerTwitterBot = innerTwitterBot;
        this.mentionFilter = Arrays.stream(mentionFilters).reduce(x -> true, Predicate::and);
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
