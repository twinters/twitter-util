package be.thomaswinters.twitter.bot;

import twitter4j.*;

import java.util.*;
import java.util.stream.Collectors;

public interface IReplyingTwitterBot {
    Optional<String> createReplyTo(Status tweet);

    default void replyToAllUnrepliedMentions() throws TwitterException {
        // Acquire mentions
        Twitter twitter = getTwitterConnection();
        List<Status> unansweredTweets = getUnansweredTweets();
        unansweredTweets.sort(new Comparator<Status>() {

            @Override
            public int compare(Status arg0, Status arg1) {
                return Long.signum(arg0.getId() - arg1.getId());
            }

        });

        if (unansweredTweets.isEmpty()) {
            return;
        }

        // Get your screenname
        String screenName = twitter.getScreenName();
        long userId = twitter.getId();

        // Reply to all statuses
        for (Status mentionTweet : unansweredTweets) {
            if (repliesToAllMentionTweets()
                    || mentionTweet.getText().toLowerCase().startsWith("@" + screenName.toLowerCase())
                    || mentionTweet.getInReplyToUserId() == userId) {
                try {
                    System.out.println("Preparing reply to tweet:\n" + mentionTweet.getText() + "\n");
                    replyTo(mentionTweet);
                } catch (TwitterException e) {
                    System.out.println("Too many replies exception? " + e);
                    break;
                }
            } else {
                System.out.println("I'm just mentioned in the following tweet. I'm not going to reply.\n"
                        + mentionTweet.getText() + "\n\n");
            }

        }
    }

    default List<Status> getUnansweredTweets() throws IllegalStateException, TwitterException {
        Twitter twitter = getTwitterConnection();
        String user = twitter.getScreenName();

        ResponseList<Status> timeline = twitter.getUserTimeline(user);
        OptionalLong minTimeline = timeline.stream().mapToLong(e -> e.getId()).min();

        Set<Long> recentlyRepliedTo = timeline.stream().map(e -> e.getInReplyToStatusId()).filter(e -> e > 0)
                .collect(Collectors.toSet());

        Paging paging = new Paging(1, Integer.max(20, recentlyRepliedTo.size()));
        List<Status> unansweredMentions = twitter.getMentionsTimeline(paging).stream()
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

    default boolean repliesToAllMentionTweets() {
        return true;
    }

    default Optional<Status> replyTo(Status mentionTweet) throws TwitterException {
        Twitter twitter = getTwitterConnection();

        // Check if this is a direct reply
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            String replyTextMention = replyText.get();
            StatusUpdate replyStatus = new StatusUpdate(replyTextMention);
            replyStatus.inReplyToStatusId(mentionTweet.getId());
            try {
                System.out.println(">> MENTION: " + mentionTweet.getText() + "\n>> MY REPLY:" + replyTextMention);
                Status newStatus = twitter.updateStatus(replyStatus);
                return Optional.of(newStatus);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();

    }

    default Twitter getTwitterConnection() {
        return TwitterFactory.getSingleton();
    }

}
