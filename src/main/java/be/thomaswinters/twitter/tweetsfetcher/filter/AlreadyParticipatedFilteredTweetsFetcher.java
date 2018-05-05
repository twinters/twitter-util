package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.sentence.SentenceUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.function.Predicate;

/**
 * Filter for filtering out tweets that has already been interacted with
 */
public class AlreadyParticipatedFilteredTweetsFetcher implements Predicate<Status> {
    private final Twitter twitter;
    private final String mentionTag;

    public AlreadyParticipatedFilteredTweetsFetcher(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.mentionTag = "@" + twitter.getScreenName();
    }


    private boolean userIsPartOfTweet(Status status) {
        return SentenceUtil.splitOnSpaces(status.getText()).anyMatch(word -> word.equals(mentionTag));
    }

    /**
     * Returns false (=not allowed) if the user has already participated in the conversation
     * @param status
     * @return
     */
    @Override
    public boolean test(Status status) {
        Status current = status;
        if (userIsPartOfTweet(status)) {
            return false;
        }
        try {
            while (current.getInReplyToStatusId() > 0L) {
                current = twitter.showStatus(current.getInReplyToStatusId());
                if (userIsPartOfTweet(current)){
                    return false;
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return false;
    }
}
