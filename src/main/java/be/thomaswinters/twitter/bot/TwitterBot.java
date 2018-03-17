package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.bot.arguments.PostingMode;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Twitterbot with its own properties instead of the singleton
 */
public abstract class TwitterBot implements ITwitterBot, IReplyingTwitterBot {
    public static final int MAX_TWEET_LENGTH = 280;

    private final Twitter twitterConnection;

    //region Constructor
    public TwitterBot(Twitter twitterConnection) {
        this.twitterConnection = twitterConnection;
    }

    public TwitterBot(File propertiesFile) throws IOException {
        this(getProperties(propertiesFile));
    }

    private static Twitter getProperties(File propertiesFile) throws IOException {
        FileInputStream fis = new FileInputStream(propertiesFile);

        Properties properties = new Properties();
        properties.load(fis);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"));
        cb.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"));
        cb.setOAuthAccessToken(properties.getProperty("oauth.accessToken"));
        cb.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        return new TwitterFactory(cb.build()).getInstance();

    }

    public TwitterBot() throws IOException {
        this(TwitterFactory.getSingleton());
    }
    //endregion

    @Override
    public Twitter getTwitterConnection() {
        return twitterConnection;
    }

    //region Execute
    public void execute(TwitterBotArguments arguments) throws TwitterException {
        if (arguments.getPostingMode().equals(PostingMode.POST)) {
            execute();
        } else if (arguments.getPostingMode().equals(PostingMode.REPLY)) {
            replyToAllUnrepliedMentions();
        }
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
}
