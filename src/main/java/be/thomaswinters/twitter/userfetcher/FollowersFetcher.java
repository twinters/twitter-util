package be.thomaswinters.twitter.userfetcher;

import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.util.paging.CursoringUserFetcher;
import twitter4j.Twitter;
import twitter4j.User;

import java.util.stream.Stream;

public class FollowersFetcher implements IUserFetcher {

    private final Twitter twitter;

    public FollowersFetcher(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Stream<User> fetchUsers() {
        long userId = TwitterUnchecker.uncheck(twitter::getId);
        return new CursoringUserFetcher(twitter,
                followerCursor ->
                        TwitterUnchecker.uncheck(twitter::getFollowersIDs, userId, followerCursor))
                .getUsers();
    }

}
