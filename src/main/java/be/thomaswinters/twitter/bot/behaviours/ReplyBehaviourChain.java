package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;

import java.util.List;
import java.util.Optional;

public class ReplyBehaviourChain implements IReplyBehaviour {
    private final List<IReplyBehaviour> behaviours;

    public ReplyBehaviourChain(List<IReplyBehaviour> behaviours) {
        this.behaviours = behaviours;
    }


    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        Optional<Boolean> success = behaviours.stream()
                .map(e -> e.reply(tweeter, tweetToReply))
                .filter(e->e)
                .findAny();
        return success.isPresent() && success.get();
    }
}
