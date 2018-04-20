package be.thomaswinters.bot.bots;

import be.thomaswinters.bot.IChatBot;
import be.thomaswinters.bot.data.IChatMessage;
import be.thomaswinters.generator.generators.IGenerator;

import java.util.Optional;

/**
 * Bot using the capabilities of the text genertor bot to reply to people (without using the chat messages)
 */
public class TextGeneratorChatBotAdaptor implements IChatBot {
    private final IGenerator<String> bot;

    public TextGeneratorChatBotAdaptor(IGenerator<String> bot) {
        this.bot = bot;
    }

    @Override
    public Optional<String> generateReply(IChatMessage message) {
        return bot.generate();
    }
}
