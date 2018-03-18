package be.thomaswinters.twitter.bot;

import be.thomaswinters.bot.ITextGeneratorBot;
import be.thomaswinters.twitter.bot.arguments.PostingMode;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Twitterbot with its own properties instead of the singleton
 */
public abstract class TwitterBot implements IReplyingTwitterBot {


    public static final int MAX_TWEET_LENGTH = 280;

    private final ITextGeneratorBot textGeneratorBot;
    private final Twitter twitterConnection;

    //region Constructor
    public TwitterBot(Twitter twitterConnection, ITextGeneratorBot textGeneratorBot) {
        this.twitterConnection = twitterConnection;
        this.textGeneratorBot = textGeneratorBot;
    }

    public TwitterBot(File propertiesFile, ITextGeneratorBot textGeneratorBot) throws IOException {
        this(getProperties(propertiesFile), textGeneratorBot);
    }

    public TwitterBot(ITextGeneratorBot textGeneratorBot) throws IOException {
        this(TwitterFactory.getSingleton(), textGeneratorBot);
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

    public Optional<Status> execute() {
        Optional<String> text = textGeneratorBot.generateText();

        if (text.isPresent()) {
            Twitter twitter = getTwitterConnection();
            StatusUpdate update = new StatusUpdate(text.get());

            try {
                Status status = twitter.updateStatus(update);
                return Optional.of(status);
            } catch (TwitterException e) {
                e.printStackTrace();
                return Optional.empty();
            }

        }
        return Optional.empty();

    }
}
