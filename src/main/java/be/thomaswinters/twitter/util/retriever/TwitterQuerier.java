package be.thomaswinters.twitter.util.retriever;

import twitter4j.*;

import java.util.Optional;
import java.util.stream.Stream;

public class TwitterQuerier implements ITweetRetriever {

    private static final boolean SHOW_REJECTION_REASONS = false;

    private final Twitter twitter;

    private final String word;
    private final Optional<String> language;
    private final Query.ResultType queryType;
    private final boolean allowURLs;

    public TwitterQuerier(Twitter twitter, Optional<String> language, Query.ResultType queryType, String word, boolean allowURLS) {
        this.twitter = twitter;
        this.word = word.toLowerCase();
        this.language = language;
        this.queryType = queryType;
        this.allowURLs = allowURLS;
    }

    public TwitterQuerier(Twitter twitter, Query.ResultType queryType, String word) {
        this(twitter, Optional.empty(), queryType, word, false);
    }

    public TwitterQuerier(Twitter twitter, String language, String word) {
        this(twitter, Optional.of(language), Query.MIXED, word, false);
    }

    public TwitterQuerier(Twitter twitter, String word) {
        this(twitter, Optional.empty(), Query.MIXED, word, false);
    }

    @Override
    public Stream<Status> retrieve(long sinceId) {

        // create a new search
        Query query = new Query(word);
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
        if (!tweet.getText().toLowerCase().contains(word.toLowerCase().replaceAll("\"", ""))) {

            if (SHOW_REJECTION_REASONS) {
                System.out.println("Bad tweet (Does not contain " + word + "): " + tweet.getText());
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
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }

        return true;
    }
}
