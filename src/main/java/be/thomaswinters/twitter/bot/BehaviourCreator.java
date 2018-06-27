package be.thomaswinters.twitter.bot;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.behaviours.*;

public class BehaviourCreator {

    public IPostBehaviour createTextGeneratorPoster(IGenerator<String> generator) {
        return new TextGeneratorPostBehaviour(generator);
    }

    public IReplyBehaviour createTextGeneratorReplier(IReactingGenerator<String, String> generator) {
        return new TextReactingGeneratorBehaviour(generator);
    }

    public IReplyBehaviour createChatMessageTextGeneratorReplier(IReactingGenerator<String, IChatMessage> generator) {
        return new ChatMessageReactingGeneratorBehaviour(generator);
    }

}
