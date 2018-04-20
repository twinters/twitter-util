package be.thomaswinters.twitter.bot.util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class TwitterLoginUtils {
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
}
