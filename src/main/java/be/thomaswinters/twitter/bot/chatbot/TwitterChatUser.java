package be.thomaswinters.twitter.bot.chatbot;

import be.thomaswinters.bot.data.IChatUser;
import twitter4j.User;

public class TwitterChatUser implements IChatUser {
    private final User user;

    public TwitterChatUser(User user) {
        this.user = user;
    }

    @Override
    public String getFullName() {
        return user.getName();
    }

    @Override
    public String getScreenName() {
        return "@" + user.getScreenName();
    }
}
