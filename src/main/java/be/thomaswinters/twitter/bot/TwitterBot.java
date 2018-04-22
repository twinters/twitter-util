package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.util.TwitterLoginUtil;
import be.thomaswinters.twitter.util.TwitterUtil;
import be.thomaswinters.twitter.util.analysis.TwitterAnalysisUtil;
import be.thomaswinters.twitter.util.retriever.TwitterMentionsRetriever;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;


public abstract class TwitterBot {

    private final Twitter twitterConnection;
    private final Collection<Consumer<Status>> postListeners = new ArrayList<>();

    //region Constructor
    public TwitterBot(Twitter twitterConnection) {
        this.twitterConnection = twitterConnection;
    }

    public TwitterBot(URL propertiesFile) throws IOException {
        this(TwitterLoginUtil.getProperties(propertiesFile));
    }
    //endregion

    //region twitterConnection
    protected Twitter getTwitterConnection() {
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
        getUnansweredTweets()
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted((e, f) -> Long.signum(e.getId() - f.getId()))
                // Reply to all mentions
                .forEachOrdered(this::replyToStatus);
    }

    private Stream<Status> getUnansweredTweets() throws IllegalStateException, TwitterException {
        long mostRecentReply = TwitterAnalysisUtil.getLastReply(twitterConnection);
        return new TwitterMentionsRetriever(twitterConnection)
                .retrieve(mostRecentReply)
                .sorted(Comparator.comparingLong(Status::getId));
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
