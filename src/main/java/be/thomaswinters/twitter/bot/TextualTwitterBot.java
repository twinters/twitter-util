package be.thomaswinters.twitter.bot;

import be.thomaswinters.generator.generators.IGenerator;
import be.thomaswinters.generator.generators.reacting.IReactingGenerator;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Optional;
import java.util.function.Function;

public abstract class TextualTwitterBot extends TwitterBot implements IGenerator<String>, IReactingGenerator<String, Status> {
    protected TextualTwitterBot(Twitter twitterConnection, ITweetsFetcher tweetsToAnswerRetriever) {
        super(twitterConnection, tweetsToAnswerRetriever);
    }

    //region post new tweet
    @Override
    public void postNewTweet() {
        Optional<String> text = prepareNewTweet();
        if (text.isPresent()) {
            try {
                getTwitterConnection().updateStatus(text.get());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    @Override
    public void replyToStatus(Status mentionTweet) {
        Optional<String> replyText = createReplyTo(mentionTweet);
        if (replyText.isPresent()) {
            try {
                reply(replyText.get(), mentionTweet);
            } catch (TwitterException twitEx) {
                if (twitEx.exceededRateLimitation()) {
                    TwitterUtil.waitForExceededRateLimitationReset();
                    replyToStatus(mentionTweet);
                } else {
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
