package be.thomaswinters.twitter.bot.loggers;

import twitter4j.Status;
import twitter4j.User;

import java.util.function.Consumer;

public class TwitterFollowPrinter implements Consumer<User> {

    private final String userName;

    public TwitterFollowPrinter(String userName) {
        this.userName = userName;
    }

    @Override
    public void accept(User user) {
        System.out.println("@" + userName + " STARTED FOLLOWING: " + user.getScreenName());
    }
}
