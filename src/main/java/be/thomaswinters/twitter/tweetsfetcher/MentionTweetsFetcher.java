package be.thomaswinters.twitter.tweetsfetcher;

import be.thomaswinters.twitter.exception.TwitterUnchecker;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.List;

public class MentionTweetsFetcher extends AbstractPagingTweetsFetcher {

    public MentionTweetsFetcher(Twitter twitter) {
        super(twitter);
    }

    @Override
    protected List<Status> getTweetsFromPage(Paging page) {
        return TwitterUnchecker.uncheck(getTwitter()::getMentionsTimeline, page);
    }
}
