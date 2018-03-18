package be.thomaswinters.bot;

import be.thomaswinters.bot.data.IChatMessage;

import java.util.Optional;

public interface IChatBot {
    Optional<String> generateReply(IChatMessage message);
}
