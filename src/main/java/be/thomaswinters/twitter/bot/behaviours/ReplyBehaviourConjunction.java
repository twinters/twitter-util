package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import twitter4j.Status;

import java.util.List;

public class ReplyBehaviourConjunction implements IReplyBehaviour {
    private final List<IReplyBehaviour> behaviours;

    public ReplyBehaviourConjunction(List<IReplyBehaviour> behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return behaviours.stream()
                .map(e -> e.reply(tweeter, tweetToReply))
                .reduce(true, (b, c) -> b && c);
    }

}
