package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.Tweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Status;

public class TextReactingGeneratorBehaviour implements IReplyBehaviour {
    private final IReactingGenerator<String, String> textGenerator;

    public TextReactingGeneratorBehaviour(IReactingGenerator<String, String> textGenerator) {
        this.textGenerator = textGenerator;
    }

    @Override
    public boolean reply(Tweeter tweeter, Status tweetToReply) {
        return textGenerator
                .generateRelated(tweetToReply.getText())
                .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, tweetToReply))
                .isPresent();
    }
}
