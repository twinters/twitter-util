package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.generator.selection.ISelector;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class QuoteRetweetingBehaviour<E> implements IPostBehaviour {
    private final IReactingGenerator<String, Status> reacter;
    private final IGenerator<Status> statusGenerator;

    public QuoteRetweetingBehaviour(IReactingGenerator<String, Status> reacter, IGenerator<Status> statusGenerator) {
        this.reacter = reacter;
        this.statusGenerator = statusGenerator;
    }

    public QuoteRetweetingBehaviour(IReactingGenerator<String, E> reacter,
                                    Function<Status, E> mapper,
                                    IGenerator<Status> statusIGenerator) {
        this.reacter = reacter.mapFrom(mapper);
        this.statusGenerator = statusIGenerator;
    }

    public QuoteRetweetingBehaviour(IReactingGenerator<String, E> replyBehaviour,
                                    Function<Status, E> mapper,
                                    ITweetsFetcher tweetsFetcher,
                                    ISelector<Status> statusSelector,
                                    Supplier<Long> fetchTweetsSinceSupplier) {
        this(replyBehaviour, mapper, tweetsFetcher.seed(fetchTweetsSinceSupplier).reduceToGenerator(statusSelector));
    }


    @Override
    public boolean post(ITweeter tweeter) {
        Optional<Status> statusToReplyTo = statusGenerator.generate();
        if (statusToReplyTo.isPresent()) {
            Optional<Status> reply = reacter.generateRelated(statusToReplyTo.get())
                    .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, statusToReplyTo.get()));
            return reply.isPresent();
        }
        return false;
    }
}
