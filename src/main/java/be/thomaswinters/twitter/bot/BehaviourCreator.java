package be.thomaswinters.twitter.bot;

import be.thomaswinters.chatbot.data.IChatMessage;
import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.generator.selection.ISelector;
import be.thomaswinters.twitter.bot.behaviours.*;
import be.thomaswinters.twitter.bot.chatbot.data.TwitterChatMessage;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BehaviourCreator {

    public static BiFunction<Status, Twitter, Status> STATUS_TO_STATUS = (e, t) -> e;
    public static BiFunction<Status, Twitter, IChatMessage> STATUS_TO_MESSAGE = (e, t) -> new TwitterChatMessage(t, e);
    public static BiFunction<Status, Twitter, String> STATUS_TO_TEXT = (e, t) -> e.getText();


    public static ITwitterBehaviour fromTextGenerator(IGenerator<String> generator) {
        return new TextGeneratorBehaviour(generator);
    }

    //region Replybehaviour from reactors
    public static IReplyBehaviour fromTextReactor(IReactingGenerator<String, String> generator) {
        return fromReactor(generator, STATUS_TO_TEXT);
    }

    public static IReplyBehaviour fromMessageReactor(IReactingGenerator<String, IChatMessage> generator) {
        return fromReactor(generator, STATUS_TO_MESSAGE);
    }

    public static IReplyBehaviour fromStatusReactor(IReactingGenerator<String, Status> generator) {
        return fromReactor(generator, STATUS_TO_STATUS);
    }

    public static <E> IReplyBehaviour fromReactor(IReactingGenerator<String, E> generator,
                                                  BiFunction<Status, Twitter, E> mapper) {
        return new ReactingGeneratorBehaviour<>(generator, mapper);
    }
    //endregion

    //region QuoteRetweeter
    public static <E> IPostBehaviour createQuoterFromReactor(IReactingGenerator<String, E> reacter,
                                                             BiFunction<Status, Twitter, E> mapper,
                                                             IGenerator<Status> statusGenerator) {
        return new QuoteRetweetingBehaviour<>(reacter, mapper, statusGenerator);
    }

    public static <E> IPostBehaviour createQuoterFromReactor(IReactingGenerator<String, E> replyBehaviour,
                                                             BiFunction<Status, Twitter, E> mapper,
                                                             ITweetsFetcher tweetsFetcher,
                                                             ISelector<Status> statusSelector,
                                                             Supplier<Long> fetchTweetsSinceSupplier) {
        return new QuoteRetweetingBehaviour<>(replyBehaviour, mapper, tweetsFetcher,
                statusSelector, fetchTweetsSinceSupplier);
    }

    public static IPostBehaviour createQuoterFromTextReactor(IReactingGenerator<String, String> reacter,
                                                             IGenerator<Status> statusGenerator) {
        return createQuoterFromReactor(reacter, STATUS_TO_TEXT, statusGenerator);
    }

    public static IPostBehaviour createQuoterFromMessageReactor(IReactingGenerator<String, IChatMessage> reacter,
                                                                IGenerator<Status> statusGenerator) {
        return createQuoterFromReactor(reacter, STATUS_TO_MESSAGE, statusGenerator);
    }

    public static IPostBehaviour createQuoterFromStatusReactor(IReactingGenerator<String, Status> reacter,
                                                               IGenerator<Status> statusGenerator) {
        return createQuoterFromReactor(reacter, STATUS_TO_STATUS, statusGenerator);
    }


    public static <E> IPostBehaviour createQuoterFromTextReactor(IReactingGenerator<String, String> replyBehaviour,
                                                                 ITweetsFetcher tweetsFetcher,
                                                                 ISelector<Status> statusSelector,
                                                                 Supplier<Long> fetchTweetsSinceSupplier) {
        return createQuoterFromReactor(replyBehaviour, STATUS_TO_TEXT, tweetsFetcher,
                statusSelector, fetchTweetsSinceSupplier);

    }

    public static <E> IPostBehaviour createQuoterFromMessageReactor(IReactingGenerator<String, IChatMessage> replyBehaviour,
                                                                    ITweetsFetcher tweetsFetcher,
                                                                    ISelector<Status> statusSelector,
                                                                    Supplier<Long> fetchTweetsSinceSupplier) {
        return createQuoterFromReactor(replyBehaviour, STATUS_TO_MESSAGE, tweetsFetcher,
                statusSelector, fetchTweetsSinceSupplier);

    }

    public static <E> IPostBehaviour createQuoterFromStatusReactor(IReactingGenerator<String, Status> replyBehaviour,
                                                                   ITweetsFetcher tweetsFetcher,
                                                                   ISelector<Status> statusSelector,
                                                                   Supplier<Long> fetchTweetsSinceSupplier) {
        return createQuoterFromReactor(replyBehaviour, STATUS_TO_STATUS, tweetsFetcher,
                statusSelector, fetchTweetsSinceSupplier);

    }
    //endregion

    ///region Chainers
    public static IPostBehaviour chainPost(IPostBehaviour... behaviours) {
        return chainPost(Arrays.asList(behaviours));
    }

    public static IPostBehaviour chainPost(List<IPostBehaviour> behaviours) {
        return new PostBehaviourChain(behaviours);
    }

    public static IReplyBehaviour chainReply(IReplyBehaviour... behaviours) {
        return chainReply(Arrays.asList(behaviours));
    }

    public static IReplyBehaviour chainReply(List<IReplyBehaviour> behaviours) {
        return new ReplyBehaviourChain(behaviours);
    }
    //endregion

}
