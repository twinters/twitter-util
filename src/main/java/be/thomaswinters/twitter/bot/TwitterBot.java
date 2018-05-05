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
    public TwitterBot(Twitter twitterConnection, Collection<Function<Twitter, ITweetsFetcher>> tweetsToAnswerRetrievers) {
        this.twitterConnection = twitterConnection;
        this.tweetsToAnswerRetrievers = ImmutableList.copyOf(
                tweetsToAnswerRetrievers
                        .stream()
                        .map(e -> e.apply(twitterConnection))
                        .collect(Collectors.toList()));
    }

    public TwitterBot(Twitter twitterConnection) {
        this(twitterConnection, Collections.singleton(MENTIONS_RETRIEVER));
    }

    //endregion

    //region twitterConnection
    public Twitter getTwitterConnection() {
        return twitterConnection;
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

    //region post new tweet
    public Optional<Status> postNewTweet() {
        Optional<String> text = prepareNewTweet();
        if (text.isPresent()) {
            try {
                System.out.println("POSTING: " + text.get());
                return Optional.of(twitterConnection.updateStatus(text.get()));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
    //endregion


    //region Reply
    public void replyToAllUnrepliedMentions() throws TwitterException {
        long mostRecentReply = TwitterAnalysisUtil.getLastReplyStatus(twitterConnection).getInReplyToStatusId();
        tweetsToAnswerRetrievers
                .stream()
                .flatMap(retriever -> retriever.retrieve(mostRecentReply))
                .filter(tweet -> tweet.getId() > mostRecentReply)
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted(Comparator.comparingLong(Status::getId))
                // Reply to all mentions
                .forEachOrdered(this::replyToStatus);
    }

    protected Optional<Status> replyToStatus(Status mentionTweet) {
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            try {
                System.out.println("REPLYING TO: " + mentionTweet.getText() + "\nREPLY: " + replyText.get() + "\n");
                return Optional.of(reply(replyText.get(), mentionTweet));
            } catch (TwitterException twitEx) {
                if (twitEx.exceededRateLimitation()) {
                    TwitterUtil.waitForExceededRateLimitationReset();
                    return replyToStatus(mentionTweet);
                } else {
                    throw new RuntimeException(twitEx);
                }
            }
        }
        return Optional.empty();
    }
    //endregion

    //region Listeners

    public void addPostListener(Consumer<Status> listener) {
        this.postListeners.add(listener);
    }

    private void notifyNewPostListeners(Status post) {
        postListeners.forEach(f -> f.accept(post));
    }
    //endregion

    //region Abstract functions
    public abstract Optional<String> createReplyTo(Status mentionTweet);

    public abstract Optional<String> prepareNewTweet();
    //endregion
}
