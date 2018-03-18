package be.thomaswinters.twitter.bot.chatbot;

import twitter4j.Status;

import java.util.Optional;

public interface ITwitterChatBot {
    Optional<String> generateReply(Status tweet);
}
