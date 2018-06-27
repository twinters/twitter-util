package be.thomaswinters.twitter.bot;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.behaviours.*;

import java.util.Arrays;
import java.util.List;

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

    public IPostBehaviour chainPost(IPostBehaviour... behaviours) {
        return chainPost(Arrays.asList(behaviours));
    }

    public IPostBehaviour chainPost(List<IPostBehaviour> behaviours) {
        return new PostBehaviourChain(behaviours);
    }

    public IReplyBehaviour chainReply(IReplyBehaviour... behaviours) {
        return chainReply(Arrays.asList(behaviours));
    }

    public IReplyBehaviour chainReply(List<IReplyBehaviour> behaviours) {
        return new ReplyBehaviourChain(behaviours);
    }

}
