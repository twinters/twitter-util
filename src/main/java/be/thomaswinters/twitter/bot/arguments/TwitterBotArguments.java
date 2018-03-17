package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.Parameter;

public class TwitterBotArguments implements ITwitterBotArguments {

    @Parameter(names = "-mode", converter = PostingModeConverter.class)
    protected PostingMode postingMode = PostingMode.POST;

    @Override
    public PostingMode getPostingMode() {
        return postingMode;
    }
}
