package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.ITwitterBotMode;
import be.thomaswinters.twitter.bot.executor.modes.PostMode;
import com.beust.jcommander.Parameter;

public class TwitterBotArguments {

    @Parameter(names = "-infinity")
    protected boolean infinity = false;

    @Parameter(names = "-debug")
    protected boolean debug = false;

    @Parameter(names = "-log")
    protected boolean log = true;

    @Parameter(names = "-mode", converter = TwitterBotModeConverter.class)
    protected ITwitterBotMode mode = new PostMode();

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

    public ITwitterBotMode getMode() {
        return mode;
    }
}
