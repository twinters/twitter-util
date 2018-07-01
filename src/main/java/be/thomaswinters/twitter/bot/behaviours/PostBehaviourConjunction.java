package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.util.List;

public class PostBehaviourConjunction implements IPostBehaviour {
    private final List<IPostBehaviour> behaviours;

    public PostBehaviourConjunction(List<IPostBehaviour> behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public boolean post(ITweeter tweeter) {
        return behaviours.stream()
                .map(e -> e.post(tweeter))
                .reduce(true, (b, c) -> b && c);
    }
}
