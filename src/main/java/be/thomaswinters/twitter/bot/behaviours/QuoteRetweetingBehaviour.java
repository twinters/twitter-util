package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.generator.selection.ISelector;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.time.Duration;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class QuoteRetweetingBehaviour<E> implements IPostBehaviour {
    private final IReactingGenerator<String, E> reacter;
    private final BiFunction<Status, Twitter, E> mapper;
    private final IGenerator<Status> statusGenerator;


    public QuoteRetweetingBehaviour(IReactingGenerator<String, E> reacter,
                                    BiFunction<Status, Twitter, E> mapper,
                                    IGenerator<Status> statusIGenerator) {
        this.reacter = reacter;
        this.mapper = mapper;
        this.statusGenerator = statusIGenerator;
    }

//    public QuoteRetweetingBehaviour(IReactingGenerator<String, Status> reacter, IGenerator<Status> statusGenerator) {
//        this(reacter, (e, t) -> e, statusGenerator);
//    }

    public QuoteRetweetingBehaviour(IReactingGenerator<String, E> replyBehaviour,
                                    BiFunction<Status, Twitter, E> mapper,
                                    ITweetsFetcher tweetsFetcher,
                                    ISelector<Status> statusSelector,
                                    Supplier<Long> fetchTweetsSinceSupplier) {
        this(replyBehaviour, mapper, tweetsFetcher
                .seed(fetchTweetsSinceSupplier).reduceToGenerator(statusSelector));
    }


    @Override
    public boolean post(ITweeter tweeter) {
        Optional<Status> statusToReplyTo = statusGenerator.generate();
        if (statusToReplyTo.isPresent()) {
            Optional<Status> reply = reacter
                    .generateRelated(mapper.apply(statusToReplyTo.get(), tweeter.getTwitterConnection()))
                    .map(text -> TwitterUnchecker.uncheck(tweeter::quoteRetweet, text, statusToReplyTo.get()));
            return reply.isPresent();
        }
        return false;
    }
}
