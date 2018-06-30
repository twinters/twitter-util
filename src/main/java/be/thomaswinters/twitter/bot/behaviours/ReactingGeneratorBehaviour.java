package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.chatbot.IChatBot;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.tweeter.ITweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.util.IExtractableChatBot;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.Optional;
import java.util.function.BiFunction;

public class ReactingGeneratorBehaviour<E> implements IReplyBehaviour, IExtractableChatBot {
    private final IReactingGenerator<String, E> textGenerator;
    private final BiFunction<Status, Twitter, E> mapper;

    public ReactingGeneratorBehaviour(IReactingGenerator<String, E> textGenerator,
                                      BiFunction<Status, Twitter, E> mapper,
                                      int maxTrials) {
        this.textGenerator = textGenerator
                .filter(maxTrials, TwitterUtil::hasValidReplyLength);
        this.mapper = mapper;
    }

    public ReactingGeneratorBehaviour(IReactingGenerator<String, E> textGenerator,
                                      BiFunction<Status, Twitter, E> mapper) {
        this(textGenerator, mapper, 10);
    }

    @Override
    public boolean reply(ITweeter tweeter, Status tweetToReply) {
        return textGenerator
                .generateRelated(mapper.apply(tweetToReply, tweeter.getTwitterConnection()))
                .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, tweetToReply))
                .isPresent();
    }

    @Override
    public Optional<IChatBot> getChatBot() {
        return Optional.of(message -> textGenerator.generateRelated((E) message));
    }
    /**
     * TODO: Add this code somehow:
     *
     Optional<String> result = twitterChatBot.generateRelated(mentionTweet);
     if (result.isPresent() && TwitterUtil.hasValidLength(result.get())) {
     return result;
     }
     return Optional.empty();
     */


    /* TODO: Deze code somehow verwerken!

        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            try {
                reply(replyText.get(), mentionTweet);
            } catch (ExcessiveTweetLengthException e) {
                e.printStackTrace();
            } catch (TwitterException twitEx) {
                if (twitEx.exceededRateLimitation()) {
                    TwitterUtil.waitForExceededRateLimitationReset();
                    replyToStatus(mentionTweet);
                } else {
                    System.out.println("PROBLEM WITH THIS TWEET: " + replyText.get());
                    throw new RuntimeException(twitEx);
                }
            }
        }
     */
}
