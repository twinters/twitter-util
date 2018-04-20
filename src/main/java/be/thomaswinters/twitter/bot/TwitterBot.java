package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.util.TwitterLoginUtils;
import twitter4j.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;


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
    public Twitter getTwitterConnection() {
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
                .stream()
                // Sort from lowest to highest such that older tweets are replied to first: more stable!
                .sorted((e, f) -> Long.signum(e.getId() - f.getId()))
                .forEachOrdered(
                        mentionTweet -> {
                            System.out.println("Preparing reply to tweet:\n" + mentionTweet.getText() + "\n");
                            try {
                                replyTo(mentionTweet);
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                        }
                );

    }

    private List<Status> getUnansweredTweets() throws IllegalStateException, TwitterException {
        String user = twitterConnection.getScreenName();

        ResponseList<Status> timeline = twitterConnection.getUserTimeline(user);
        OptionalLong minTimeline = timeline.stream().mapToLong(Status::getId).min();

        Set<Long> recentlyRepliedTo = timeline.stream().map(Status::getInReplyToStatusId).filter(e -> e > 0)
                .collect(Collectors.toSet());

        Paging paging = new Paging(1, Integer.max(20, recentlyRepliedTo.size()));
        List<Status> unansweredMentions = twitterConnection.getMentionsTimeline(paging).stream()
                .filter(e -> !recentlyRepliedTo.contains(e.getId())).collect(Collectors.toList());

        // Make the mentions at least as recent as the least recent recent reply
        if (minTimeline.isPresent()) {
            unansweredMentions = unansweredMentions.stream().filter(e -> e.getId() > minTimeline.getAsLong())
                    .collect(Collectors.toList());
        }

        // Print all unanswered mentions
        if (!unansweredMentions.isEmpty()) {
            System.out.println("Unanswered mentions: " + unansweredMentions.size() + "\n"
                    + unansweredMentions.stream().map(e -> ">> " + e.getText()).collect(Collectors.joining("\n"))
                    + "\n");
        }

        return unansweredMentions;

    }

    public Optional<Status> replyTo(Status mentionTweet) throws TwitterException {
        Twitter twitter = getTwitterConnection();

        // Check if this is a direct reply
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            String replyTextWithMention = replyText
                    .map(reply -> "@" + mentionTweet.getUser().getScreenName() + " " + reply)
                    .get();
            StatusUpdate replyStatus = new StatusUpdate(replyTextWithMention);
            replyStatus.inReplyToStatusId(mentionTweet.getId());
            try {
                System.out.println(">> MENTION: " + mentionTweet.getText() + "\n>> MY REPLY:" + replyTextWithMention);
                Status newStatus = twitter.updateStatus(replyStatus);
                return Optional.of(newStatus);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();

    }


    public abstract Optional<String> createReplyTo(Status mentionTweet);

    public abstract Optional<String> prepareNewTweet();
    //endregion

}
