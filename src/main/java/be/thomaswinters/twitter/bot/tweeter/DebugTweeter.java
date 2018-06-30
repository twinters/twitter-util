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
        print("QUOTE RETWEET TO: \"" + toTweet.getText() + "\"", status);
        return null;
    }

    @Override
    public Status tweet(String status) throws TwitterException {
        print("TWEET", status);
        return null;
    }

    @Override
    public Status reply(String replyText, Status toTweet) throws TwitterException {
        print("REPLY TO: \"" + toTweet.getText() + "\"", replyText);
        return null;
    }

    @Override
    public void follow(User user) throws TwitterException {
        print("FOLLOW " + user.getScreenName());

    }

    private void print(String action, String result) {
        System.out.println("\n>> " + debuggerName + " " + action + (result.length() > 0 ? "\n>> RESULT: " + result + "\n" : ""));
    }

    private void print(String action) {
        print(action, "");

    }

    @Override
    public Twitter getTwitterConnection() {
        return null;
    }
}
