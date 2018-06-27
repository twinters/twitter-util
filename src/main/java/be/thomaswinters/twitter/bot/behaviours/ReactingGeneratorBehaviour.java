package be.thomaswinters.twitter.bot.behaviours;

import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.bot.Tweeter;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ReactingGeneratorBehaviour<E> implements IReplyBehaviour {
    private final IReactingGenerator<String, E> textGenerator;
    private final BiFunction<Status,Twitter,E> mapper;

    public ReactingGeneratorBehaviour(IReactingGenerator<String, E> textGenerator, BiFunction<Status, Twitter, E> mapper) {
        this.textGenerator = textGenerator;
        this.mapper = mapper;
    }

    @Override
    public boolean reply(Tweeter tweeter, Status tweetToReply) {
        return textGenerator
                .generateRelated(mapper.apply(tweetToReply,tweeter.getTwitterConnection()))
                .map(text -> TwitterUnchecker.uncheck(tweeter::reply, text, tweetToReply))
                .isPresent();
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
