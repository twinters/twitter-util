package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.Parameter;

public class TwitterBotArguments {

    @Parameter(names = "-mode", converter = PostingModeConverter.class)
    protected PostingMode postingMode = PostingMode.POST;

    @Parameter(names = "-infinity")
    protected boolean infinity = false;

    @Parameter(names = "-debug")
    protected boolean debug = false;


    @Parameter(names = "-amountOfTimes")
    protected int amountOfTimes = 1;


    public PostingMode getPostingMode() {
        return postingMode;
    }

    public boolean isInfinity() {
        return infinity;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getAmountOfTimes() {
        return amountOfTimes;
    }
}
