package be.thomaswinters.twitter.util.scouter;

import java.util.Collection;

import twitter4j.Status;
import twitter4j.TwitterException;

public interface ITweetScouter {
	Collection<Status> scout(long sinceId) throws TwitterException;

	default Collection<Status> scout() throws TwitterException {
		return scout(0);
	};
}
