package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;

public class RetryingReplyBehaviour implements IReplyBehaviour {
    private final IReplyBehaviour innerReplyBehaviour;
    private final int maxRetries;

    public RetryingReplyBehaviour(IReplyBehaviour innerReplyBehaviour, int maxRetries) {
        this.innerReplyBehaviour = innerReplyBehaviour;
        this.maxRetries = maxRetries;
    }


    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return RetryingTwitterBehaviour.retry(() -> innerReplyBehaviour.reply(tweeter, tweetToReply), maxRetries);
    }
}
