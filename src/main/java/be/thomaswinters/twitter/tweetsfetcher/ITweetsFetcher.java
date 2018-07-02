package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.generator.streamgenerator.reacting.IReactingStreamGenerator;
import be.thomaswinters.sentence.SentenceUtil;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.filter.FilteredTweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.filter.RandomFilter;
import be.thomaswinters.twitter.tweetsfetcher.filter.UserFilteredTweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface ITweetsFetcher extends IReactingStreamGenerator<Status, Long> {

    @Override
    default Stream<Status> generateStream(Long sinceId) {
        return retrieve(sinceId);
    }

    Stream<Status> retrieve(long sinceId);

    default Stream<Status> retrieve() {
        return retrieve(1L);
    }

    default ITweetsFetcher combineWith(ITweetsFetcher... fetchers) {
        return new TweetsFetcherCombiner(
                Stream.concat(Stream.of(this), Stream.of(fetchers))
                        .collect(Collectors.toList()));
    }

    default ITweetsFetcher filter(Predicate<Status> filter) {
        // TODO: Remove filteredtweetsfetched and use super.filter
        return new FilteredTweetsFetcher(this, filter);
    }

    default ITweetsFetcher filterOutOwnTweets(Twitter twitter) {
        return TwitterUnchecker.uncheck(UserFilteredTweetsFetcher::new, this, twitter);
    }

    default ITweetsFetcher filterOutMessagesWithWords(Collection<String> words) {
        return filter(status -> SentenceUtil.getWordsStream(status.getText()).noneMatch(words::contains));
    }

    default ITweetsFetcher filterOutRetweets() {
        return TwitterUnchecker.uncheck(this::filter, FilteredTweetsFetcher.RETWEETS_REJECTER);
    }

    default ITweetsFetcher filterOutReplies() {
        return TwitterUnchecker.uncheck(this::filter, FilteredTweetsFetcher.REPLY_REJECTER);
    }

    default ITweetsFetcher filterRandomlyIf(Twitter twitter, Predicate<Status> shouldFilter, int chances, int outOf) {
        RandomFilter randomFilter = new RandomFilter(twitter, chances, outOf);
        return TwitterUnchecker.uncheck(this::filter,
                status -> !shouldFilter.test(status) || randomFilter.test(status));
    }

    default ITweetsFetcher filterRandomly(Twitter twitter, int chances, int outOf) {
        RandomFilter randomFilter = new RandomFilter(twitter, chances, outOf);
        return filter(randomFilter);
    }

    default TweetsFetcherCache cache(TemporalAmount timeToCache) {
        return new TweetsFetcherCache(this, timeToCache);
    }

    default RepliedToTweetsIdFetcher mapToRepliedToIds() {
        return new RepliedToTweetsIdFetcher(this);
    }

    default ITweetsFetcher peek(Consumer<Status> consumer) {
        return filter(e -> {
            consumer.accept(e);
            return true;
        });
    }

    default ITweetsFetcher distinct() {
        return input -> generateStream(input).distinct();
    }
}
