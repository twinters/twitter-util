package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.sentence.SentenceUtil;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.function.Predicate;

/**
 * Filter for filtering out tweets that has already been interacted with
 */
public class AlreadyParticipatedFilter implements Predicate<Status> {
    private final Twitter twitter;
    private final String mentionTag;

    public AlreadyParticipatedFilter(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.mentionTag = "@" + twitter.getScreenName();
    }


    private boolean userIsPartOfTweet(Status status) {
        return SentenceUtil.splitOnSpaces(status.getText()).anyMatch(word -> word.equals(mentionTag));
    }

    /**
     * Returns false (=not allowed) if the user has already participated in the conversation
     *
     * @param status
     * @return
     */
    @Override
    public boolean test(Status status) {
        Status current = status;
        if (userIsPartOfTweet(status)) {
            return false;
        }
        while (current.getInReplyToStatusId() > 0L) {
            current = TwitterUnchecker.uncheck(twitter::showStatus, current.getInReplyToStatusId());
            assert current != null;
            if (userIsPartOfTweet(current)) {
                return false;
            }
        }
        return true;
    }
}
