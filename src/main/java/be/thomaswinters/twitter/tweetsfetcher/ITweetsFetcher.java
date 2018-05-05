package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.tweetsfetcher.filter.FilteredTweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.filter.UserFilteredTweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

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

    default ITweetsFetcher filterOutOwnTweets(Twitter twitter) throws TwitterException {
        return new UserFilteredTweetsFetcher(this, twitter);
    }

    default ITweetsFetcher filterOutRetweets() throws TwitterException {
        return new FilteredTweetsFetcher(this, FilteredTweetsFetcher.RETWEETS_REJECTER);
    }

    default ITweetsFetcher filterOutReplies() throws TwitterException {
        return new FilteredTweetsFetcher(this, FilteredTweetsFetcher.REPLY_REJECTER);
    }
}
