package be.thomaswinters.twitter.bot;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.exception.ExcessiveTweetLengthException;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;

public abstract class TextualTwitterBot extends TwitterBot implements IGenerator<String>, IReactingGenerator<String, Status> {
    protected TextualTwitterBot(Twitter twitterConnection, ITweetsFetcher tweetsToAnswerRetriever) {
        super(twitterConnection, tweetsToAnswerRetriever);
    }

    //region post new tweet
    @Override
    public void postNewTweet() {
        prepareNewTweet().ifPresent(e -> TwitterUnchecker.uncheck(this::tweet, e));
    }
    //endregion

    @Override
    public void replyToStatus(Status mentionTweet) {
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
    }

    //region Abstract functions
    public abstract Optional<String> createReplyTo(Status mentionTweet);

    public abstract Optional<String> prepareNewTweet();

    @Override
    public Optional<String> generate() {
        return prepareNewTweet();
    }

    @Override
    public Optional<String> generateRelated(Status input) {
        return createReplyTo(input);
    }
    //endregion
}
