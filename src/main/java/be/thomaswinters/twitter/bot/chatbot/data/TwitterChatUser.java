package be.thomaswinters.twitter.bot.chatbot.data;

import be.thomaswinters.chatbot.data.IChatUser;
import twitter4j.User;

import java.util.Optional;

public class TwitterChatUser implements IChatUser {
    private final User user;

    public TwitterChatUser(User user) {
        this.user = user;
    }

    @Override
    public Optional<String> getFullName() {
        return Optional.of(user.getName());
    }

    @Override
    public String getScreenName() {
        return "@" + user.getScreenName();
    }

    @Override
    public long getId() {
        return user.getId();
    }

    @Override
    public String toString() {
        return getScreenName();
    }
}
