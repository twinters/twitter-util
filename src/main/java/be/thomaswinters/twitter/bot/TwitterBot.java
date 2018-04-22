package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.util.TwitterLoginUtil;
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
        StatusUpdate reply = new StatusUpdate("@" + toTweet.getUser().getScreenName() + " " + replyText);
        reply.inReplyToStatusId(toTweet.getId());
        Status post = twitterConnection.updateStatus(reply);
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
        System.out.println(mostRecentReply);
        return new TwitterMentionsRetriever(twitterConnection)
                .retrieve(mostRecentReply);

//        ResponseList<Status> timeline = twitterConnection.getUserTimeline(twitterConnection.getScreenName());
//        OptionalLong minTimeline = timeline.stream().mapToLong(Status::getId).min();
//
//        Paging paging = new Paging(1, Integer.max(20, recentlyRepliedTo.size()));
//        Stream<Status> unansweredMentions =
//                twitterConnection
//                        .getMentionsTimeline(paging)
//                        .stream()
//                        .filter(e -> !recentlyRepliedTo.contains(e.getId()));
//        System.out.println("Recently replied to: " + recentlyRepliedTo);
//        System.out.println("unansweredMentions: " + unansweredMentions.collect(Collectors.toList()));
//
//        // Make the mentions at least as recent as the least recent recent reply
//        if (minTimeline.isPresent()) {
//            unansweredMentions = unansweredMentions
//                    .filter(e -> e.getId() > minTimeline.getAsLong());
//        }
//        return unansweredMentions;
    }

    protected Optional<Status> replyToStatus(Status mentionTweet) {
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            try {
                System.out.println("REPLYING TO: " + mentionTweet.getText() + "\nREPLY: " + replyText.get() + "\n");
                return Optional.of(reply(replyText.get(), mentionTweet));
            } catch (TwitterException twitEx) {
                twitEx.printStackTrace();
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
