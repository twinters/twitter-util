package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class RetryingPostBehaviour implements IPostBehaviour {
    private final IPostBehaviour innerPostBehaviour;
    private final int maxRetries;

    public RetryingPostBehaviour(IPostBehaviour innerPostBehaviour, int maxRetries) {
        this.innerPostBehaviour = innerPostBehaviour;
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean post(ITweeter tweeter) {
        return IntStream.range(0, maxRetries)
                .mapToObj(x -> robustPost(tweeter))
                .dropWhile(e -> !e)
                .findFirst()
                .orElse(false);

    }

    private boolean robustPost(ITweeter tweeter) {
        try {
            return innerPostBehaviour.post(tweeter);
        } catch (UncheckedTwitterException e) {
            // Tweet too long
            if (e.getErrorCode() == 186) {
                return false;
            }
            // If exceeded rate limitation: sleep
            else if (e.exceededRateLimitation()) {
                System.out.println("Exceeded Twitter rate limitation: sleeping for 10 minutes");
                try {
                    TimeUnit.MINUTES.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return false;
            } else {
                throw e;
            }
        }
    }
}
