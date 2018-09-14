package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.selection.RouletteWheelSelection;
import be.thomaswinters.generator.selection.Weighted;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Picks a behaviour ar random, and if that doesn't work, it will pick another one
 */
public class PostBehaviourDisjunction implements IPostBehaviour {
    private final List<Weighted<? extends IPostBehaviour>> behaviours;

    public PostBehaviourDisjunction(List<Weighted<? extends IPostBehaviour>> behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public boolean post(ITweeter tweeter) {

        List<Weighted<? extends IPostBehaviour>> options = new ArrayList<>(behaviours);

        while (!options.isEmpty()) {
            Optional<IPostBehaviour> behaviour =
                    RouletteWheelSelection.selectWeightedRouletteWheel(options.stream());
            if (behaviour.isPresent()) {
                boolean result = behaviour.get().post(tweeter);
                if (result) {
                    return true;
                }
                // Filter out this option
                options = options.stream()
                        .filter(e -> !e.getElement().equals(behaviour.get()))
                        .collect(Collectors.toList());
            }
        }

        return false;
    }
}
