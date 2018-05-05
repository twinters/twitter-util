package be.thomaswinters.twitter.util.retriever;

import com.google.common.collect.ImmutableList;
import twitter4j.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchTweetsFetcher implements ITweetsFetcher {

    private static final boolean SHOW_REJECTION_REASONS = false;

    private final Twitter twitter;

    private final ImmutableList<String> words;
    private final Optional<String> language;
    private final Query.ResultType queryType;
    private final boolean allowURLs;

    public SearchTweetsFetcher(Twitter twitter, Optional<String> language, Query.ResultType queryType, List<String> words, boolean allowURLS) {
        this.twitter = twitter;
        this.words = ImmutableList.copyOf(words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()));
        this.language = language;
        this.queryType = queryType;
        this.allowURLs = allowURLS;
    }

    public SearchTweetsFetcher(Twitter twitter, Optional<String> language, Query.ResultType queryType, String word, boolean allowURLS) {
        this(twitter, language, queryType, Collections.singletonList(word), allowURLS);
    }

    public SearchTweetsFetcher(Twitter twitter, Query.ResultType queryType, String word) {
        this(twitter, Optional.empty(), queryType, word, false);
    }

    public SearchTweetsFetcher(Twitter twitter, String language, String word) {
        this(twitter, Optional.of(language), Query.MIXED, word, false);
    }

    public SearchTweetsFetcher(Twitter twitter, List<String> words) {
        this(twitter, Optional.empty(), Query.MIXED, words, false);
    }

    public SearchTweetsFetcher(Twitter twitter, String word) {
        this(twitter, Optional.empty(), Query.MIXED, word, false);
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {

        // create a new search
        Query query = new Query(words.stream().collect(Collectors.joining(" ")));
        query.setCount(100);
        if (sinceId > 0) {
            query.setSinceId(sinceId);
        }
        language.ifPresent(query::setLang);

        // get the results from that search
        query.setResultType(queryType);
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }

        return result.getTweets().stream()
                // Map to original tweet
                .map(e -> e.isRetweet() ? e.getRetweetedStatus() : e)
                .distinct()
                // Check if recent enough
                .filter(e -> e.getId() > sinceId)
                .filter(this::isOkay);
    }

    private boolean isOkay(Status tweet) {
        // if (tweet.getUser().getName().toLowerCase().contains(word)) {
        // if (SHOW_REJECTION_REASONS) {
        // System.out.println("Bad tweet (username contains " + word + "): " +
        // tweet.getText());
        // }
        // return false;
        // }
        // if (Stream.of(tweet.getUserMentionEntities()).map(e -> e.getName() +
        // e.getScreenName())
        // .anyMatch(e -> e.contains(word))) {
        // if (SHOW_REJECTION_REASONS) {
        // System.out.println("Bad tweet (mentions username containing " + word
        // + "): " + tweet.getText());
        // }
        // return false;
        // }
        if (tweet.isRetweetedByMe()) {
            if (SHOW_REJECTION_REASONS) {
                System.out.println("Bad tweet (retweeted by me): " + tweet.getText());
            }
            return false;
        }
        if (!allowURLs && (tweet.getText().contains("https://") || tweet.getText().contains("http://")
                || tweet.getText().contains("t.co"))) {

            if (SHOW_REJECTION_REASONS) {
                System.out.println("Bad tweet (URL): " + tweet.getText());
            }
            return false;
        }
        if (!words.stream().allMatch(word -> tweet.getText().toLowerCase().contains(word.toLowerCase().replaceAll("\"", "")))) {

            if (SHOW_REJECTION_REASONS) {
                System.out.println("Bad tweet (Does not contain " + words + "): " + tweet.getText());
            }
            return false;
        }

        try {
            if (tweet.getUser().getScreenName().equals(twitter.getScreenName())) {
                if (SHOW_REJECTION_REASONS) {
                    System.out.println("Bad tweet (Posted by me): " + tweet.getText());
                }
                return false;
            }
        } catch (IllegalStateException | TwitterException e1) {
            e1.printStackTrace();
        }

        return true;
    }
}
