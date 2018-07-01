package be.thomaswinters.twitter.tweetsfetcher.filter;

import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Filter for filtering out tweets that has already been interacted with
 */
public class AlreadyParticipatedFilter implements Predicate<Status> {
    private final Twitter twitter;
    private final String screenName;
    private final int maxNumberLookback;

    public AlreadyParticipatedFilter(Twitter twitter, int maxNumberLookback) throws TwitterException {
        this.twitter = twitter;
//        this.mentionTag = "@" + twitter.getScreenName();
        this.screenName = twitter.getScreenName();
        this.maxNumberLookback = maxNumberLookback;
    }


    public AlreadyParticipatedFilter(Twitter twitter) throws TwitterException {
        this(twitter, Integer.MAX_VALUE);
    }

    private boolean hasTweeted(Status status) {
        return status.getUser().getScreenName().equals(screenName);
    }

    public boolean hasParticipatedInConversation(Status status) {
        Status current = status;
        if (hasTweeted(status)) {
            return true;
        }
        int numberChecked = 1;
        while (current.getInReplyToStatusId() > 0L && numberChecked < maxNumberLookback) {
            try {
                current = twitter.showStatus(current.getInReplyToStatusId());
            } catch (TwitterException e) {
                // Status removed
                if (e.getErrorCode() == 144) {
                    return false;
                }
                // Not authorised to view status: blocked?
                else if (e.getErrorCode() == 179) {
                    return true;
                } else if (e.exceededRateLimitation()) {
                    System.out.println("Exceeded Twitter limit: sleeping for 15 minutes!");
                    try {
                        TimeUnit.MINUTES.sleep(15);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    throw new UncheckedTwitterException(e);
                }
            }
            assert current != null;
            if (hasTweeted(current)) {
                return true;
            }
            numberChecked += 1;
        }
        return false;

    }

    /**
     * Returns false (=not allowed) if the user has already participated in the conversation
     *
     * @param status
     * @return
     */
    @Override
    public boolean test(Status status) {
        return !hasParticipatedInConversation(status);
    }
}
