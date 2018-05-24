package be.thomaswinters.twitter.bot.loggers;

import twitter4j.Status;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TweetReplyPrinter implements BiConsumer<Status, Status> {
    @Override
    public void accept(Status status, Status toTweet) {
        System.out.println("@" + status.getUser().getScreenName() + " REPLIED TO THIS TWEET : \n"
                + "@"+toTweet.getUser().getScreenName() + ": " + toTweet.getText() + "\n"
                + "WITH THIS REPLY:\n" + status.getText()
        );
    }
}
