package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.behaviours.IPostBehaviour;
import be.thomaswinters.twitter.bot.behaviours.IReplyBehaviour;
import be.thomaswinters.twitter.bot.behaviours.ITwitterBehaviour;
import be.thomaswinters.twitter.bot.executor.TwitterBotExecutor;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.bot.tweeter.Tweeter;
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


public class TwitterBot {

    public static final Function<Twitter, ITweetsFetcher> MENTIONS_RETRIEVER = MentionTweetsFetcher::new;
    public static final Function<Twitter, Supplier<Long>> LAST_REPLIED_TO_SUPPLIER = twitter ->
            () -> TwitterUnchecker.uncheck(TwitterUtil::getLastReplyStatus, twitter)
                    .map(Status::getInReplyToStatusId)
                    .orElse(1L);


    private final Twitter twitterConnection;

    private final IPostBehaviour postBehaviour;
    private final IReplyBehaviour replyBehaviour;

    private final ITweetsFetcher tweetsToAnswerRetriever;
    private final Supplier<Long> lastRepliedToSupplier;


    private final Tweeter tweeter;

    //region Constructor
    public TwitterBot(Twitter twitterConnection, IPostBehaviour postBehaviour, IReplyBehaviour replyBehaviour, ITweetsFetcher tweetsToAnswerRetrievers, Supplier<Long> lastRepliedToSupplier) {
        this.twitterConnection = twitterConnection;

        this.postBehaviour = postBehaviour;
        this.replyBehaviour = replyBehaviour;

        this.tweetsToAnswerRetriever = tweetsToAnswerRetrievers;
        this.lastRepliedToSupplier = lastRepliedToSupplier;

        this.tweeter = new Tweeter(twitterConnection);
    }

    public TwitterBot(Twitter twitterConnection, IPostBehaviour postBehaviour, IReplyBehaviour replyBehaviour, ITweetsFetcher tweetsToAnswerRetrievers) {
        this(twitterConnection, postBehaviour, replyBehaviour, tweetsToAnswerRetrievers, LAST_REPLIED_TO_SUPPLIER.apply(twitterConnection));
    }

    public TwitterBot(Twitter twitterConnection, IPostBehaviour postBehaviour, IReplyBehaviour replyBehaviour) {
        this(twitterConnection, postBehaviour, replyBehaviour, MENTIONS_RETRIEVER.apply(twitterConnection));
    }

    public TwitterBot(Twitter twitterConnection, ITwitterBehaviour twitterBehaviour) {
        this(twitterConnection, twitterBehaviour, twitterBehaviour, MENTIONS_RETRIEVER.apply(twitterConnection));
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
        replyToAllUnrepliedMentions(getTweeter());
    }

    public void replyToAllUnrepliedMentions(ITweeter tweeter) {
        long mostRecentRepliedToStatus = lastRepliedToSupplier.get();

        tweetsToAnswerRetriever
                .retrieve(mostRecentRepliedToStatus)
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted(Comparator.comparingLong(Status::getId))
                .distinct()
                // Reply to all mentions
                .forEachOrdered(e -> replyToStatus(e, tweeter));
    }
    //endregion


    //region abstract methods
    public void postNewTweet() {
        postNewTweet(getTweeter());
    }

    public void postNewTweet(ITweeter customTweeter) {
        postBehaviour.post(customTweeter);
    }

    public void replyToStatus(long mentionTweet) {
        replyToStatus(TwitterUnchecker.uncheck(getTwitterConnection()::showStatus, mentionTweet));
    }

    public void replyToStatus(Status mentionTweet) {
        replyToStatus(mentionTweet, getTweeter());
    }

    public void replyToStatus(Status mentionTweet, ITweeter tweeter) {
        replyBehaviour.reply(tweeter, mentionTweet);
    }
    //endregion

    public TwitterBotExecutor createExecutor() {
        return new TwitterBotExecutor(this);
    }


}
