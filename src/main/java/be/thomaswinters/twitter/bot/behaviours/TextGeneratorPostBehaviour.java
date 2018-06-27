package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.twitter.bot.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;

public class TextGeneratorPostBehaviour implements IPostBehaviour {
    private final IGenerator<String> textGenerator;

    public TextGeneratorPostBehaviour(IGenerator<String> textGenerator) {
        this.textGenerator = textGenerator;
    }

    @Override
    public boolean post(ITweeter tweeter) {
        return textGenerator
                .generate()
                .map(text -> TwitterUnchecker.uncheck(tweeter::tweet, text))
                .isPresent();
    }
}
