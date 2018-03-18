package be.thomaswinters.bot;

import be.thomaswinters.bot.data.IChatMessage;

public interface IChatBot {
    String generateReply(IChatMessage message);
}
