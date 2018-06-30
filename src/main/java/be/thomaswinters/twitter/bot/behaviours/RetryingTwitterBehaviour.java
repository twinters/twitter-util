package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class RetryingTwitterBehaviour implements ITwitterBehaviour {
    private final ITwitterBehaviour innerReplyBehaviour;
    private final int maxRetries;

    public RetryingTwitterBehaviour(ITwitterBehaviour innerReplyBehaviour, int maxRetries) {
        this.innerReplyBehaviour = innerReplyBehaviour;
        this.maxRetries = maxRetries;
    }

    public static boolean retry(Supplier<Boolean> action, int maxRetries) {
        return IntStream.range(0, maxRetries)
                .mapToObj(x -> TwitterUtil.robustTwitterAction(action))
                .dropWhile(e -> !e)
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return retry(() -> innerReplyBehaviour.reply(tweeter, tweetToReply), maxRetries);
    }

    @Override
    public boolean post(ITweeter tweeter) {
        return retry(() -> innerReplyBehaviour.post(tweeter), maxRetries);
    }
}
