package be.thomaswinters.twitter.bot;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.behaviours.*;
import be.thomaswinters.twitter.bot.chatbot.data.TwitterChatMessage;
import twitter4j.Status;

import java.util.Arrays;
import java.util.List;

public class BehaviourCreator {

    public static IPostBehaviour createTextGeneratorPoster(IGenerator<String> generator) {
        return new TextGeneratorPostBehaviour(generator);
    }

    public static IReplyBehaviour createTextGeneratorReplier(IReactingGenerator<String, String> generator) {
        return new ReactingGeneratorBehaviour<>(generator, (e, t) -> e.getText());
    }

    public static IReplyBehaviour createChatMessageTextGeneratorReplier(IReactingGenerator<String, IChatMessage> generator) {
        return new ReactingGeneratorBehaviour<>(generator, (e, t) -> new TwitterChatMessage(t, e));
    }

    public static IReplyBehaviour createStatusTextGeneratorReplier(IReactingGenerator<String, Status> generator) {
        return new ReactingGeneratorBehaviour<>(generator, (e, t) -> e);
    }

    public static IPostBehaviour chainPost(IPostBehaviour... behaviours) {
        return chainPost(Arrays.asList(behaviours));
    }

    public static IPostBehaviour chainPost(List<IPostBehaviour> behaviours) {
        return new PostBehaviourChain(behaviours);
    }

    public static IReplyBehaviour chainReply(IReplyBehaviour... behaviours) {
        return chainReply(Arrays.asList(behaviours));
    }

    public static IReplyBehaviour chainReply(List<IReplyBehaviour> behaviours) {
        return new ReplyBehaviourChain(behaviours);
    }

}
