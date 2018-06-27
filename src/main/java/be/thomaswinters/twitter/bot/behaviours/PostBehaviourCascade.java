package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.twitter.bot.Tweeter;

import java.util.List;
import java.util.Optional;

public class PostBehaviourCascade implements IPostBehaviour {
    private final List<IPostBehaviour> behaviours;

    public PostBehaviourCascade(List<IPostBehaviour> behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public boolean post(Tweeter tweeter) {
        Optional<Boolean> success = behaviours.stream()
                .map(e -> e.post(tweeter))
                .filter(e->e)
                .findAny();
        return success.isPresent() && success.get();
    }
}
