package be.thomaswinters.twitter.util;

import be.thomaswinters.chatbot.IChatBot;

import java.util.Optional;

public interface IExtractableChatBot {
    Optional<IChatBot> getChatBot();
}
