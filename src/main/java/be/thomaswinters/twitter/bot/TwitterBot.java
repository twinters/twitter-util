package be.thomaswinters.twitter.bot;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Twitterbot with its own properties instead of the singleton
 */
public abstract class TwitterBot {


    public static final int MAX_TWEET_LENGTH = 280;
    private final Twitter twitterConnection;

    //region Constructor
    public TwitterBot(Twitter twitterConnection) {
        this.twitterConnection = twitterConnection;
    }

    public TwitterBot(URL propertiesFile) throws IOException {
        this(getProperties(propertiesFile));
    }

    public static Twitter getProperties(URL propertiesFile) throws IOException {

        Properties properties = new Properties();
        InputStream stream = propertiesFile.openStream();
        properties.load(stream);
        stream.close();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"));
        cb.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"));
        cb.setOAuthAccessToken(properties.getProperty("oauth.accessToken"));
        cb.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        return new TwitterFactory(cb.build()).getInstance();

    }

    //endregion


//    public Optional<Status> execute() throws TwitterException {
//
//        // Use as execute
//        return execute(getLastRealTweet());
//    }

    public long getLastTweet() throws TwitterException {
        Twitter twitter = getTwitterConnection();
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream().mapToLong(e -> e.getId()).max().orElse(0l);
    }


    /**
     * Returns most recent tweet, excluding replies and retweets
     *
     * @return
     * @throws TwitterException
     */
    public long getLastRealTweet() throws TwitterException {
        Twitter twitter = getTwitterConnection();
        ResponseList<Status> timeline = twitter.getUserTimeline(twitter.getScreenName());
        return timeline.stream().filter(e -> !e.getText().startsWith("@") && !e.getText().startsWith("RT : ")).mapToLong(e -> e.getId()).max().orElse(0l);
    }

    public boolean isValidTweet(String text) {
        return text.length() <= 140;
    }

    public Twitter getTwitterConnection() {
        return twitterConnection;
    }

    //region Execute


    public abstract Optional<String> prepareNewTweet();

    public Optional<Status> postNewTweet() {
        Optional<String> text = prepareNewTweet();

        if (text.isPresent()) {
            Twitter twitter = getTwitterConnection();
            StatusUpdate update = new StatusUpdate(text.get());

            try {
                Status status = twitter.updateStatus(update);
                return Optional.of(status);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();

    }
    //endregion

    protected Status tweet(String status) throws TwitterException {
        return getTwitterConnection().updateStatus(status);
    }

    protected Status reply(String status, Status toTweet) throws TwitterException {
        StatusUpdate reply = new StatusUpdate("@" + toTweet.getUser().getScreenName() + " " + status);
        reply.inReplyToStatusId(toTweet.getId());
        return getTwitterConnection().updateStatus(reply);
    }

    public static boolean isValidLength(String text) {
        return text.length() <= MAX_TWEET_LENGTH;
    }


    //region Reply

    public void replyToAllUnrepliedMentions() throws TwitterException {
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

    public List<Status> getUnansweredTweets() throws IllegalStateException, TwitterException {
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

    public boolean repliesToAllMentionTweets() {
        return true;
    }

    public Optional<Status> replyTo(Status mentionTweet) throws TwitterException {
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

    public abstract Optional<String> createReplyTo(Status mentionTweet);

    //endregion

}
