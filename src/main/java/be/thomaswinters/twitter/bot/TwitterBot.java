package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.MentionTweetsFetcher;
import be.thomaswinters.twitter.util.TwitterUtil;
import be.thomaswinters.twitter.util.analysis.TwitterAnalysisUtil;
import com.google.common.collect.ImmutableList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class TwitterBot {

    public static final Function<Twitter, ITweetsFetcher> MENTIONS_RETRIEVER = MentionTweetsFetcher::new;

    private final Twitter twitterConnection;
    private final Collection<ITweetsFetcher> tweetsToAnswerRetrievers;
    private final Collection<Consumer<Status>> postListeners = new ArrayList<>();

    //region Constructor
    @SafeVarargs
    public TwitterBot(Twitter twitterConnection, Function<Twitter, ITweetsFetcher>... tweetsToAnswerRetrievers) {
        this.twitterConnection = twitterConnection;
        List<Function<Twitter, ITweetsFetcher>> fetchers = Arrays.asList(tweetsToAnswerRetrievers);
        if (fetchers.isEmpty()) {
            fetchers = Collections.singletonList(MENTIONS_RETRIEVER);
        }
        this.tweetsToAnswerRetrievers = ImmutableList.copyOf(
                fetchers
                        .stream()
                        .map(e -> e.apply(twitterConnection))
                        .collect(Collectors.toList()));
    }

    //endregion

    //region twitterConnection
    public Twitter getTwitterConnection() {
        return twitterConnection;
    }

    protected Status quoteRetweet(String status, Status toTweet) throws TwitterException {
        return tweet(status + " " + TwitterUtil.getQuoteRetweetUrl(toTweet));
    }

    protected Status tweet(String status) throws TwitterException {
        Status post = twitterConnection.updateStatus(status);
        notifyNewPostListeners(post);
        return post;
    }

    protected Status reply(String replyText, Status toTweet) throws TwitterException {
        StatusUpdate replyPreparation = new StatusUpdate("@" + toTweet.getUser().getScreenName() + " " + replyText);
        replyPreparation.inReplyToStatusId(toTweet.getId());
        Status post = twitterConnection.updateStatus(replyPreparation);
        notifyNewPostListeners(post);
        return post;
    }
    //endregion


    //region Reply
    public void replyToAllUnrepliedMentions() throws TwitterException {
        long mostRecentRepliedToStatus = TwitterAnalysisUtil.getLastReplyStatus(twitterConnection)
                .map(Status::getInReplyToStatusId)
                .orElse(1L);

        tweetsToAnswerRetrievers
                .stream()
                .flatMap(retriever -> retriever.retrieve(mostRecentRepliedToStatus))
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted(Comparator.comparingLong(Status::getId))
                .distinct()
                // Reply to all mentions
                .forEachOrdered(this::replyToStatus);
    }
    //endregion


    //region abstract methods
    public abstract Optional<Status> postNewTweet();
    protected abstract Optional<Status> replyToStatus(Status mentionTweet);
    //endregion


    //region Listeners
    public void addPostListener(Consumer<Status> listener) {
        this.postListeners.add(listener);
    }

    private void notifyNewPostListeners(Status post) {
        postListeners.forEach(f -> f.accept(post));
    }
    //endregion

}
