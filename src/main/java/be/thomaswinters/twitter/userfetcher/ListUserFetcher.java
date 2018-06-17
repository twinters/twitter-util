package be.thomaswinters.twitter.userfetcher;

import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Twitter;
import twitter4j.User;

import java.util.stream.Stream;

public class ListUserFetcher implements IUserFetcher {

    private final Twitter twitter;
    private final long listId;

    public ListUserFetcher(Twitter twitter, long listId) {
        this.twitter = twitter;
        this.listId = listId;
    }

    @Override
    public Stream<User> fetchUsers() {
        return TwitterUnchecker.uncheck(twitter::getUserListMembers, listId, 5000, -1).stream();
    }
}
