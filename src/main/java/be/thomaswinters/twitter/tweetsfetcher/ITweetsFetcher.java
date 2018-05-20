package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.generator.streamgenerator.reacting.IReactingStreamGenerator;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.filter.FilteredTweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.filter.UserFilteredTweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ITweetsFetcher extends IReactingStreamGenerator<Status, Long> {

    @Override
    default Stream<Status> generateStream(Long sinceId) {
        return retrieve(sinceId);
    }

    Stream<Status> retrieve(long sinceId);

    default Stream<Status> retrieve() {
        return retrieve(1l);
    }

    default ITweetsFetcher combineWith(ITweetsFetcher fetcher) {
        return new TweetsFetcherCombiner(this, fetcher);
    }

    default ITweetsFetcher filter(Predicate<Status> filter) {
        return new FilteredTweetsFetcher(this, filter);
    }

    default ITweetsFetcher filterOutOwnTweets(Twitter twitter) {
        return TwitterUnchecker.uncheck(UserFilteredTweetsFetcher::new,this, twitter);
    }

    default ITweetsFetcher filterOutRetweets() {
        return  TwitterUnchecker.uncheck(FilteredTweetsFetcher::new,this, FilteredTweetsFetcher.RETWEETS_REJECTER);
    }

    default ITweetsFetcher filterOutReplies() {
        return  TwitterUnchecker.uncheck(FilteredTweetsFetcher::new, this, FilteredTweetsFetcher.REPLY_REJECTER);
    }

}
