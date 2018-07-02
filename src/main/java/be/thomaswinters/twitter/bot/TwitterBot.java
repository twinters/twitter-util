package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.behaviours.IPostBehaviour;
import be.thomaswinters.twitter.bot.behaviours.IReplyBehaviour;
import be.thomaswinters.twitter.bot.behaviours.ITwitterBehaviour;
import be.thomaswinters.twitter.bot.executor.TwitterBotExecutor;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.bot.tweeter.Tweeter;
import be.thomaswinters.twitter.bot.util.LastRepliedToSupplier;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.MentionTweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class TwitterBot {

    public static final Function<Twitter, ITweetsFetcher> MENTIONS_RETRIEVER = MentionTweetsFetcher::new;


    private final Twitter twitterConnection;

    private final IPostBehaviour postBehaviour;
    private final IReplyBehaviour replyBehaviour;

    private final ITweetsFetcher tweetsToAnswerRetriever;
    private final Supplier<Long> lastRepliedToSupplier;


    private final Tweeter tweeter;


    private final List<Consumer<Status>> inspectedTweetToAnswerListeners = new ArrayList<>();

    //region Constructor
    public TwitterBot(Twitter twitterConnection,
                      IPostBehaviour postBehaviour,
                      IReplyBehaviour replyBehaviour,
                      ITweetsFetcher tweetsToAnswerRetrievers,
                      Supplier<Long> lastRepliedToSupplier) {
        this.twitterConnection = twitterConnection;

        this.postBehaviour = postBehaviour;
        this.replyBehaviour = replyBehaviour;

        this.tweetsToAnswerRetriever = tweetsToAnswerRetrievers;
        this.lastRepliedToSupplier = lastRepliedToSupplier;

        this.tweeter = new Tweeter(twitterConnection);
    }

    public TwitterBot(Twitter twitterConnection,
                      IPostBehaviour postBehaviour,
                      IReplyBehaviour replyBehaviour,
                      ITweetsFetcher tweetsToAnswerRetrievers) {
        this(twitterConnection, postBehaviour, replyBehaviour, tweetsToAnswerRetrievers, new LastRepliedToSupplier(twitterConnection));
        addInspectedTweetToAnswerListener(
                ((LastRepliedToSupplier) this.lastRepliedToSupplier)::updateLastInspectedTweetToAnswer);
    }

    public TwitterBot(Twitter twitterConnection,
                      IPostBehaviour postBehaviour,
                      IReplyBehaviour replyBehaviour) {
        this(twitterConnection, postBehaviour, replyBehaviour, MENTIONS_RETRIEVER.apply(twitterConnection));
    }

    public TwitterBot(Twitter twitterConnection,
                      ITwitterBehaviour twitterBehaviour) {
        this(twitterConnection, twitterBehaviour, twitterBehaviour, MENTIONS_RETRIEVER.apply(twitterConnection));
    }

    //endregion

    //region twitterConnection
    public Twitter getTwitterConnection() {
        return twitterConnection;
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

    public void replyToStatus(long tweetToReplyTo) {
        replyToStatus(TwitterUnchecker.uncheck(getTwitterConnection()::showStatus, tweetToReplyTo));
    }

    public void replyToStatus(Status tweetToReplyTo) {
        replyToStatus(tweetToReplyTo, getTweeter());
    }

    public void replyToStatus(Status tweetToReplyTo, ITweeter customTweeter) {
        replyBehaviour.reply(customTweeter, tweetToReplyTo);
        notifyInspectedTweetToAnswer(tweetToReplyTo);
    }
    //endregion

    public IPostBehaviour getPostBehaviour() {
        return postBehaviour;
    }

    public IReplyBehaviour getReplyBehaviour() {
        return replyBehaviour;
    }

    public TwitterBotExecutor createExecutor() {
        return new TwitterBotExecutor(this);
    }

    //region inspected listener

    private void notifyInspectedTweetToAnswer(Status status) {
        inspectedTweetToAnswerListeners.forEach(e -> e.accept(status));
    }

    public void addInspectedTweetToAnswerListener(Consumer<Status> listener) {
        inspectedTweetToAnswerListeners.add(listener);
    }

    public boolean removeInspectedTweetToAnswerListener(Consumer<Status> listener) {
        return inspectedTweetToAnswerListeners.remove(listener);
    }

    //endregion

}
