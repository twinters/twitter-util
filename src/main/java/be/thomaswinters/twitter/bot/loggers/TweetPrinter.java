package be.thomaswinters.twitter.bot.loggers;

import twitter4j.Status;

import java.util.function.Consumer;

public class TweetPrinter implements Consumer<Status> {
    @Override
    public void accept(Status status) {
        System.out.println("@" + status.getUser().getScreenName() + " TWEETED: " + status.getText());
    }
}
