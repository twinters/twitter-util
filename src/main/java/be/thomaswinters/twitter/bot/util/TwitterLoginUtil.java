package be.thomaswinters.twitter.bot.util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class TwitterLoginUtil {
    public static Twitter getProperties(URL propertiesFile) throws IOException {

        Properties properties = new Properties();
        InputStream stream = propertiesFile.openStream();
        properties.load(stream);
        stream.close();

        return getTwitter(
                properties.getProperty("oauth.consumerKey"),
                properties.getProperty("oauth.consumerSecret"),
                properties.getProperty("oauth.accessToken"),
                properties.getProperty("oauth.accessTokenSecret"));

    }

    public static Twitter getTwitterFromEnvironment(String environmentPrefix) {
        return getTwitter(
                System.getenv(environmentPrefix + "consumerKey"),
                System.getenv(environmentPrefix + "consumerSecret"),
                System.getenv(environmentPrefix + "accessToken"),
                System.getenv(environmentPrefix + "accessTokenSecret")
        );
    }

    public static Twitter getTwitterFromEnvironment() {
        return getTwitterFromEnvironment("oauth.");
    }

    public static Twitter getTwitter(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        ConfigurationBuilder cb =
                new ConfigurationBuilder()
                        .setOAuthConsumerKey(consumerKey)
                        .setOAuthConsumerSecret(consumerSecret)
                        .setOAuthAccessToken(accessToken)
                        .setOAuthAccessTokenSecret(accessTokenSecret)
                        .setTweetModeExtended(true);
        return new TwitterFactory(cb.build()).getInstance();

    }

}
