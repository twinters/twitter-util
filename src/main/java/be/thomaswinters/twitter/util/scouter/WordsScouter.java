package be.thomaswinters.twitter.util.scouter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import twitter4j.Status;
import twitter4j.TwitterException;

public class WordsScouter implements ITweetScouter {
	private final ImmutableList<WordScouter> wordScouters;

	public WordsScouter(Collection<? extends String> words) {
		this.wordScouters = ImmutableList
				.copyOf(words.stream().map(e -> new WordScouter(e)).collect(Collectors.toList()));
	}
	public WordsScouter(String... words) {
		this(Arrays.asList(words));
	}

	public Collection<Status> scout(long sinceId) throws TwitterException {
		Set<Status> result = new HashSet<Status>();
		for (WordScouter s : wordScouters) {
			result.addAll(s.scout(sinceId));
		}
		return result;
	}
}
