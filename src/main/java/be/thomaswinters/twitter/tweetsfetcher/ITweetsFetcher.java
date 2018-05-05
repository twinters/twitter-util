package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.exception.TwitterExceptionUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.filter.FilteredTweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.filter.UserFilteredTweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ITweetsFetcher {
    Stream<Status> retrieve(long sinceId);

    default Stream<Status> retrieve() {
        return retrieve(1l);
    }

    default ITweetsFetcher filter(Predicate<Status> filter) {
        return new FilteredTweetsFetcher(this, filter);
    }

    default ITweetsFetcher filterOutOwnTweets(Twitter twitter) {
        return TwitterExceptionUnchecker.uncheck(UserFilteredTweetsFetcher::new,this, twitter);
    }

    default ITweetsFetcher filterOutRetweets() {
        return  TwitterExceptionUnchecker.uncheck(FilteredTweetsFetcher::new,this, FilteredTweetsFetcher.RETWEETS_REJECTER);
    }

    default ITweetsFetcher filterOutReplies() {
        return  TwitterExceptionUnchecker.uncheck(FilteredTweetsFetcher::new, this, FilteredTweetsFetcher.REPLY_REJECTER);
    }
}
