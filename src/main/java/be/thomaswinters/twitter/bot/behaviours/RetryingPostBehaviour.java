package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;

public class RetryingPostBehaviour implements IPostBehaviour {
    private final IPostBehaviour innerPostBehaviour;
    private final int maxRetries;

    public RetryingPostBehaviour(IPostBehaviour innerPostBehaviour, int maxRetries) {
        this.innerPostBehaviour = innerPostBehaviour;
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean post(ITweeter tweeter) {
        return RetryingTwitterBehaviour.retry(() -> innerPostBehaviour.post(tweeter), maxRetries);
    }
}
