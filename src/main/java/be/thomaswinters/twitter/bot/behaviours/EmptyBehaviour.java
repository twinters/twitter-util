package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;

public class EmptyBehaviour implements ITwitterBehaviour {
    @Override
    public boolean post(ITweeter tweeter) {
        return false;
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return false;
    }
}
