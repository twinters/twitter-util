package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.selection.Weighted;

public interface ITwitterBehaviour extends IPostBehaviour, IReplyBehaviour {

    @Override
    default Weighted<ITwitterBehaviour> weight(double weight) {
        return new Weighted<>(this, weight);
    }

    @Override
    default ITwitterBehaviour retry(int amountOfTimes) {
        return new RetryingTwitterBehaviour(this, amountOfTimes);
    }
}
