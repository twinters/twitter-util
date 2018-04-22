package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.Parameter;

public class TwitterBotArguments {

    @Parameter(names = "-infinity")
    protected boolean infinity = false;

    @Parameter(names = "-debug")
    protected boolean debug = false;

    @Parameter(names = "-post")
    protected boolean post = false;

    @Parameter(names = "-reply")
    protected boolean reply = false;


    @Parameter(names = "-amountOfTimes")
    protected int amountOfTimes = 1;

    public boolean isInfinity() {
        return infinity;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getAmountOfTimes() {
        return amountOfTimes;
    }

    public boolean isPosting() {
        return post;
    }

    public boolean isReplying() {
        return reply;
    }
}
