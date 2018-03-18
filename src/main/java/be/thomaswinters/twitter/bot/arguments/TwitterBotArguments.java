package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.Parameter;

public class TwitterBotArguments {

    @Parameter(names = "-mode", converter = PostingModeConverter.class)
    protected PostingMode postingMode = PostingMode.POST;

    @Parameter(names = "-infinity")
    protected boolean infinity = false;


    public PostingMode getPostingMode() {
        return postingMode;
    }

    public boolean isInfinity() {
        return infinity;
    }
}
