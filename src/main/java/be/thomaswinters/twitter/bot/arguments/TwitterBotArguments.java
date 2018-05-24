package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.Parameter;

public class TwitterBotArguments {

    @Parameter(names = "-infinity")
    protected boolean infinity = false;

    @Parameter(names = "-debug")
    protected boolean debug = false;

    @Parameter(names = "-log")
    protected boolean log = true;

    @Parameter(names = "-mode", converter = PostingModeConverter.class)
    protected PostingMode mode = PostingMode.POST;


    @Parameter(names = "-times")
    protected int amountOfTimes = 1;

    public boolean isInfinity() {
        return infinity;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLog() {
        return log;
    }

    public int getAmountOfTimes() {
        return amountOfTimes;
    }

    public boolean isPosting() {
        return mode.allowsPosting();
    }

    public boolean isReplying() {
        return mode.allowsReplying();
    }
}
