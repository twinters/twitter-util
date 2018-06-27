package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.Tweeter;
import be.thomaswinters.twitter.bot.chatbot.data.TwitterChatMessage;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Status;

public class ChatMessageReactingGeneratorBehaviour implements IReplyBehaviour {
    private final IReactingGenerator<String, IChatMessage> textGenerator;

    public ChatMessageReactingGeneratorBehaviour(IReactingGenerator<String, IChatMessage> textGenerator) {
        this.textGenerator = textGenerator;
    }

    @Override
    public boolean reply(Tweeter tweeter, Status tweetToReply) {
        return textGenerator
                .generateRelated(new TwitterChatMessage(tweeter.getTwitterConnection(), tweetToReply))
                .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, tweetToReply))
                .isPresent();
    }
}
