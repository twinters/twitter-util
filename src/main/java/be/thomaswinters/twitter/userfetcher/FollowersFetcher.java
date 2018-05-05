package be.thomaswinters.twitter.userfetcher;

import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import be.thomaswinters.twitter.util.paging.CursoringUserFetcher;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.stream.Stream;

public class FollowersFetcher implements IUserFetcher {

    private final Twitter twitter;

    public FollowersFetcher(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Stream<User> fetchUsers() {
        try {
            long userId = twitter.getId();
            return new CursoringUserFetcher(twitter,
                    followerCursor -> {
                        try {
                            return twitter.getFollowersIDs(userId, followerCursor);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                            throw new UncheckedTwitterException(e);
                        }
                    })
                    .getUsers();
        } catch (TwitterException e) {
            e.printStackTrace();
            throw new UncheckedTwitterException(e);
        }
    }

}
