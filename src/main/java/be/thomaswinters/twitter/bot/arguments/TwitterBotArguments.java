package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.ITwitterBotMode;
import be.thomaswinters.twitter.bot.executor.modes.PostMode;
import com.beust.jcommander.Parameter;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

public class TwitterBotArguments {

    @Parameter(names = "-infinity")
    protected boolean infinity = false;

    @Parameter(names = "-debug")
    protected boolean debug = false;

    @Parameter(names = "-log")
    protected boolean log = true;

    @Parameter(names = "-mode", converter = TwitterBotModeConverter.class)
    protected ITwitterBotMode mode = new PostMode();

    @Parameter(names = "-postTimes")
    protected int postTimes = 1;

    @Parameter(names = "-replyWait", converter = TemporalAmountConverter.class)
    protected TemporalAmount replyWait = Duration.ofMinutes(0);

    @Parameter(names = "-runDuration", converter = TemporalAmountConverter.class)
    protected TemporalAmount runDuration = Duration.ofMinutes(0);

    // Range of amount of posts to post

    public boolean isInfinity() {
        return infinity;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLogging() {
        return log;
    }

    public int getPostTimes() {
        return postTimes;
    }

    public TemporalAmount getReplyWait() {
        return replyWait;
    }

    public TemporalAmount getRunDuration() {
        return runDuration;
    }

    public ITwitterBotMode getMode() {
        return mode;
    }
}
