package be.thomaswinters.twitter.bot.tweeter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class DebugTweeter implements ITweeter {
    private final String debuggerName;

    public DebugTweeter(String debuggerName) {
        this.debuggerName = debuggerName;
    }
    public DebugTweeter() {
        this("DEBUG");
    }


    @Override
    public Status quoteRetweet(String status, Status toTweet) throws TwitterException {
        System.out.println(">> "+debuggerName+" QUOTE RETWEET:\n" + status + "\nTO: " + toTweet.getText() + "\n");
        return null;
    }

    @Override
    public Status tweet(String status) throws TwitterException {
        System.out.println(">> "+debuggerName+" POST:\n" + status + "\n");
        return null;
    }

    @Override
    public Status reply(String replyText, Status toTweet) throws TwitterException {
        System.out.println(">> "+debuggerName+" QUOTE REPLY:\n" + replyText + "\nTO: " + toTweet.getText() + "\n");
        return null;
    }

    @Override
    public void follow(User user) throws TwitterException {
        System.out.println(">> "+debuggerName+" FOLLOW:\n" + user.getScreenName() + "\n");

    }

    @Override
    public Twitter getTwitterConnection() {
        return null;
    }
}
