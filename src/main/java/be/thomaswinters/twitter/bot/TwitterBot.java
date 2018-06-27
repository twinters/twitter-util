package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.executor.TwitterBotExecutor;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.MentionTweetsFetcher;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Comparator;
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

    private final Tweeter tweeter;

    //region Constructor
    public TwitterBot(Twitter twitterConnection, ITweetsFetcher tweetsToAnswerRetrievers, Supplier<Long> lastRepliedToSupplier) {
        this.twitterConnection = twitterConnection;
        this.tweetsToAnswerRetriever = tweetsToAnswerRetrievers;
        this.lastRepliedToSupplier = lastRepliedToSupplier;
        this.tweeter = new Tweeter(twitterConnection);
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
        return tweeter.quoteRetweet(status, toTweet);
    }

    protected Status tweet(String status) throws TwitterException {
        return tweeter.tweet(status);
    }

    protected Status reply(String replyText, Status toTweet) throws TwitterException {
        return tweeter.reply(replyText, toTweet);
    }

    public Tweeter getTweeter() {
        return tweeter;
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

    public TwitterBotExecutor createExecutor() {
        return new TwitterBotExecutor(this);
    }


}
