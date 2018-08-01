package be.thomaswinters.twitter.bot.chatbot;

import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import twitter4j.Status;

import java.util.Optional;

public interface ITwitterChatBot extends IReactingGenerator<String,Status> {
    Optional<String> generateReply(Status tweet);

    @Override
    default Optional<String> generate(Status input) {
        return generateReply(input);
    }
}
