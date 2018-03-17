package be.thomaswinters.twitter.util.scouter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class WordScouter implements ITweetScouter {

	private static final boolean SHOW_REJECTION_REASONS = false;

	private static final Twitter TWITTER = TwitterFactory.getSingleton();

	private final String word;
	private final Optional<String> language;
	private final Query.ResultType queryType;
	private final boolean allowURLs;

	public WordScouter(Optional<String> language, Query.ResultType queryType, String word, boolean allowURLS) {
		this.word = word.toLowerCase();
		this.language = language;
		this.queryType = queryType;
		this.allowURLs = allowURLS;
	}

	public WordScouter(Query.ResultType queryType, String word) {
		this(Optional.empty(), queryType, word, false);
	}

	public WordScouter(String language, String word) {
		this(Optional.of(language), Query.MIXED, word, false);
	}

	public WordScouter(String word) {
		this(Optional.empty(), Query.MIXED, word, false);
	}

	@Override
	public Collection<Status> scout(long sinceId) throws TwitterException {

		// access the twitter API using your twitter4j.properties file
		Twitter twitter = TwitterFactory.getSingleton();

		// create a new search
		Query query = new Query(word);
		query.setCount(100);
		if (sinceId > 0) {
			query.setSinceId(sinceId);
		}
		if (language.isPresent()) {
			query.setLang(language.get());
		}

		// get the results from that search
		query.setResultType(queryType);
		QueryResult result = twitter.search(query);

		Set<Status> goodTweets = result.getTweets().stream()
				// Map to original tweet
				.map(e -> e.isRetweet() ? e.getRetweetedStatus() : e).distinct()
				// Check if recent enough
				.filter(e -> e.getId() > sinceId).filter(e -> isOkay(e)).collect(Collectors.toSet());
		return goodTweets;
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
			if (tweet.getUser().getScreenName().equals(TWITTER.getScreenName())) {
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
