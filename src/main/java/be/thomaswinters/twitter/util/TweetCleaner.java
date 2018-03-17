package be.thomaswinters.twitter.util;

import java.util.regex.Pattern;

public class TweetCleaner {
	private final static Pattern mentionRemover = Pattern.compile("@(\\w+)",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	public static String cleanTweet(String text) {
		return removeRetweet(removeMentions(text));
	}

	public static String removeRetweet(String text) {
		if (text.startsWith("RT : ")) {
			return text.substring(5).trim();
		}
		return text.trim();
	}

	public static String removeMentions(String text) {
		return mentionRemover.matcher(text).replaceAll("");
	}

}
