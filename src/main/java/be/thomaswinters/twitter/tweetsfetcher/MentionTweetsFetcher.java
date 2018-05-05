package be.thomaswinters.twitter.tweetsfetcher;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class MentionTweetsFetcher extends AbstractPagingTweetsFetcher {

    public MentionTweetsFetcher(Twitter twitter) {
        super(twitter);
    }

    @Override
    protected List<Status> getTweetsFromPage(Paging page) throws TwitterException {
        return getTwitter().getMentionsTimeline(page);
    }


}
