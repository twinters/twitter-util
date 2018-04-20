package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.util.TwitterLoginUtils;
import twitter4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class TwitterBot {

    private final Twitter twitterConnection;

    //region Constructor
    public TwitterBot(Twitter twitterConnection) {
        this.twitterConnection = twitterConnection;
    }

    public TwitterBot(URL propertiesFile) throws IOException {
        this(TwitterLoginUtils.getProperties(propertiesFile));
    }
    //endregion

    //region twitterConnection
    protected Twitter getTwitterConnection() {
        return twitterConnection;
    }

    protected Status tweet(String status) throws TwitterException {
        return twitterConnection.updateStatus(status);
    }

    protected Status reply(String status, Status toTweet) throws TwitterException {
        StatusUpdate reply = new StatusUpdate("@" + toTweet.getUser().getScreenName() + " " + status);
        reply.inReplyToStatusId(toTweet.getId());
        return twitterConnection.updateStatus(reply);
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
        ResponseList<Status> timeline = twitterConnection.getUserTimeline(twitterConnection.getScreenName());
        OptionalLong minTimeline = timeline.stream().mapToLong(Status::getId).min();

        Set<Long> recentlyRepliedTo = timeline
                .stream()
                .map(Status::getInReplyToStatusId)
                .filter(e -> e > 0)
                .collect(Collectors.toSet());

        Paging paging = new Paging(1, Integer.max(20, recentlyRepliedTo.size()));
        Stream<Status> unansweredMentions =
                twitterConnection
                        .getMentionsTimeline(paging)
                        .stream()
                        .filter(e -> !recentlyRepliedTo.contains(e.getId()));

        // Make the mentions at least as recent as the least recent recent reply
        if (minTimeline.isPresent()) {
            unansweredMentions = unansweredMentions
                    .filter(e -> e.getId() > minTimeline.getAsLong());
        }
        return unansweredMentions;
    }

    private Optional<Status> replyToStatus(Status mentionTweet) {
        // Check if this is a direct reply
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            try {
                return Optional.of(reply(replyText.get(), mentionTweet));
            } catch (TwitterException twitEx) {
                twitEx.printStackTrace();
            }
        }
        return Optional.empty();
    }
    //endregion

    //region Abstract functions
    public abstract Optional<String> createReplyTo(Status mentionTweet);

    public abstract Optional<String> prepareNewTweet();
    //endregion
}
