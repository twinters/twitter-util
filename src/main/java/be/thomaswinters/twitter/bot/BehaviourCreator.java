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

    public static ITwitterBehaviour fromTextGenerator(IGenerator<String> generator) {
        return new TextGeneratorBehaviour(generator);
    }

    public static IReplyBehaviour fromReactingTextGenerator(IReactingGenerator<String, String> generator) {
        return new ReactingGeneratorBehaviour<>(generator, (e, t) -> e.getText());
    }

    public static IReplyBehaviour fromReactingMessageGenerator(IReactingGenerator<String, IChatMessage> generator) {
        return new ReactingGeneratorBehaviour<>(generator, (e, t) -> new TwitterChatMessage(t, e));
    }

    public static IReplyBehaviour fromReactingStatusGenerator(IReactingGenerator<String, Status> generator) {
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
