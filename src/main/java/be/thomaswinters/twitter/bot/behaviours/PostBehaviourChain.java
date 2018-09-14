package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.util.List;
import java.util.Optional;

/**
 * Keeps trying postbehaviours until one returns a value
 */
public class PostBehaviourChain implements IPostBehaviour {
    private final List<IPostBehaviour> behaviours;

    public PostBehaviourChain(List<IPostBehaviour> behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public boolean post(ITweeter tweeter) {
        Optional<Boolean> success = behaviours.stream()
                .map(e -> e.post(tweeter))
                .filter(e->e)
                .findAny();
        return success.isPresent() && success.get();
    }
}
