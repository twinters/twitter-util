package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.exception.ExcessiveTweetLengthException;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.MentionTweetsFetcher;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public abstract class TwitterBot {

    public static final Function<Twitter, ITweetsFetcher> MENTIONS_RETRIEVER = MentionTweetsFetcher::new;
    public static final Function<Twitter, Supplier<Long>> LAST_REPLIED_TO_SUPPLIER = twitter ->
            () -> TwitterUnchecker.uncheck(TwitterUtil::getLastReplyStatus, twitter)
                    .map(Status::getInReplyToStatusId)
                    .orElse(1L);

    private final Twitter twitterConnection;
    private final ITweetsFetcher tweetsToAnswerRetriever;
    private final Supplier<Long> lastRepliedToSupplier;
    private final Collection<Consumer<Status>> postListeners = new ArrayList<>();
    private final Collection<BiConsumer<Status, Status>> replyListeners = new ArrayList<>();

    //region Constructor
    public TwitterBot(Twitter twitterConnection, ITweetsFetcher tweetsToAnswerRetrievers, Supplier<Long> lastRepliedToSupplier) {
        this.twitterConnection = twitterConnection;
        this.tweetsToAnswerRetriever = tweetsToAnswerRetrievers;
        this.lastRepliedToSupplier = lastRepliedToSupplier;
    }

    public TwitterBot(Twitter twitterConnection, ITweetsFetcher tweetsToAnswerRetrievers) {
        this(twitterConnection, tweetsToAnswerRetrievers, LAST_REPLIED_TO_SUPPLIER.apply(twitterConnection));
    }

    public TwitterBot(Twitter twitterConnection) {
        this(twitterConnection, MENTIONS_RETRIEVER.apply(twitterConnection));
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
        String fullReplyText = "@" + toTweet.getUser().getScreenName() + " " + replyText;

        if (!TwitterUtil.hasValidLength(fullReplyText)) {
            throw new ExcessiveTweetLengthException(fullReplyText);
        }
        StatusUpdate replyPreparation = new StatusUpdate(fullReplyText);
        replyPreparation.inReplyToStatusId(toTweet.getId());
        Status post = twitterConnection.updateStatus(replyPreparation);
        notifyNewReplyListeners(post, toTweet);
        return post;
    }
    //endregion


    //region Reply
    public void replyToAllUnrepliedMentions() {
        long mostRecentRepliedToStatus = lastRepliedToSupplier.get();

        tweetsToAnswerRetriever
                .retrieve(mostRecentRepliedToStatus)
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted(Comparator.comparingLong(Status::getId))
                .distinct()
                // Reply to all mentions
                .forEachOrdered(this::replyToStatus);
    }
    //endregion


    //region abstract methods
    public abstract void postNewTweet();

    public void replyToStatus(long mentionTweet) {
        replyToStatus(TwitterUnchecker.uncheck(getTwitterConnection()::showStatus, mentionTweet));
    }

    public abstract void replyToStatus(Status mentionTweet);
    //endregion


    //region Listeners
    public void addPostListener(Consumer<Status> listener) {
        this.postListeners.add(listener);
    }

    private void notifyNewPostListeners(Status post) {
        postListeners.forEach(f -> f.accept(post));
    }

    public void addReplyListener(BiConsumer<Status, Status> listener) {
        this.replyListeners.add(listener);
    }

    private void notifyNewReplyListeners(Status reply, Status toTweet) {
        replyListeners.forEach(f -> f.accept(reply, toTweet));
    }
    //endregion

}
