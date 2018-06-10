package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.sentence.SentenceUtil;
import be.thomaswinters.twitter.exception.TwitterUnchecker;
import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.concurrent.ExecutionException;
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


    private final Cache<Status, Boolean> partOfCache = CacheBuilder.newBuilder().maximumSize(5000).build();
    private boolean userIsPartOfTweet(Status status) {
        try {
            return partOfCache.get(status,
                    ()-> SentenceUtil.splitOnSpaces(status.getText()).anyMatch(word -> word.equals(mentionTag)));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
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
            try {
                current = twitter.showStatus(current.getInReplyToStatusId());
            } catch (TwitterException e) {
                // Status removed
                if (e.getErrorCode() == 144) {
                    System.out.println("Problems with previous of " + current.getText());
                    return true;
                }
                // Not authorised to view status: blocked?
                else if (e.getErrorCode() == 179){
                    return false;
                } else {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    throw new UncheckedTwitterException(e);
                }
            }
            assert current != null;
            if (userIsPartOfTweet(current)) {
                return false;
            }
        }
        return true;
    }
}
