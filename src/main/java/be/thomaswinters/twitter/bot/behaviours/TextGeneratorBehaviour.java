package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;

public class TextGeneratorBehaviour implements ITwitterBehaviour {
    private final IGenerator<String> textGenerator;

    public TextGeneratorBehaviour(IGenerator<String> textGenerator, int maxTrials) {
        this.textGenerator = textGenerator
                .filter(maxTrials, TwitterUtil::hasValidLength);
    }

    public TextGeneratorBehaviour(IGenerator<String> textGenerator) {
        this(textGenerator, 10);
    }


    @Override
    public boolean post(ITweeter tweeter) {
        return textGenerator
                .generate()
                .map(text -> TwitterUnchecker.uncheck(tweeter::tweet, text))
                .isPresent();
    }


    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return textGenerator
                .generate()
                .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, tweetToReply))
                .isPresent();
    }
}
