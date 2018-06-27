package be.thomaswinters.twitter.bot.tweeter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public interface ITweeter {
    //region Performers
    Status quoteRetweet(String status, Status toTweet) throws TwitterException;

    Status tweet(String status) throws TwitterException;

    Status reply(String replyText, Status toTweet) throws TwitterException;

    void follow(User user) throws TwitterException;

    Twitter getTwitterConnection();
}
