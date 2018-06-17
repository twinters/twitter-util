package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.generator.streamgenerator.IStreamGenerator;
import be.thomaswinters.twitter.userfetcher.ListUserFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.stream.Stream;

/**
 * This class queries all the users from a list, and then gets statuses with the given constraints
 * (e.g. retweets or not, replies or not)
 * This is due to the ListTweetsFetcher only fetching replies if the reply is to someone in the group.
 */
public class AdvancedListTweetsFetcher implements ITweetsFetcher {

    private final IStreamGenerator<UserTweetsFetcher> userTweetsFetchers;

    public AdvancedListTweetsFetcher(Twitter twitter, long listId, boolean allowRetweets, boolean allowReplies) {
        this.userTweetsFetchers =
                new ListUserFetcher(twitter, listId)
                        .map(user -> new UserTweetsFetcher(twitter, user.getScreenName(), allowRetweets, allowReplies));

    }


    @Override
    public Stream<Status> retrieve(long sinceId) {
        return userTweetsFetchers
                .flatMap(userFetcher -> userFetcher.retrieve(sinceId))
                .generateStream();
    }
}
