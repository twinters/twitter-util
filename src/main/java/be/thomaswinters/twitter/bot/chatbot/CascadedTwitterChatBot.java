package be.thomaswinters.twitter.bot.chatbot;

import com.google.common.collect.ImmutableList;
import twitter4j.Status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class CascadedTwitterChatBot implements ITwitterChatBot {
    private final Collection<ITwitterChatBot> chatbots;

    public CascadedTwitterChatBot(Collection<ITwitterChatBot> chatbots) {
        this.chatbots = ImmutableList.copyOf(chatbots);
    }

    public CascadedTwitterChatBot(ITwitterChatBot... chatbots) {
        this(Arrays.asList(chatbots));
    }

    @Override
    public Optional<String> generateReply(Status tweet) {
        return chatbots.stream()
                .map(e -> e.generateReply(tweet))
                .reduce(Optional.empty(), (e, f) -> e.isPresent() ? e : f);
    }
}
