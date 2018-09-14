package be.thomaswinters.twitter.tweetsfetcher.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AlreadyParticipatedFilterTest {

    private Twitter twitter;
    private User twitterBotUser;
    private User otherUser;
    private User otherUser2;

    private AlreadyParticipatedFilter filter1;
    private AlreadyParticipatedFilter filter2;

    @BeforeEach
    void setUp() throws TwitterException {

        // Mock twitterbot user
        twitterBotUser = Mockito.mock(User.class);
        when(twitterBotUser.getScreenName()).thenReturn("TwitterBot");
        otherUser = Mockito.mock(User.class);
        when(otherUser.getScreenName()).thenReturn("OtherBot");
        otherUser2 = Mockito.mock(User.class);
        when(otherUser2.getScreenName()).thenReturn("SecondBot");

        // Mock Twitter
        twitter = Mockito.mock(Twitter.class);
        when(twitter.getScreenName()).thenReturn("TwitterBot");

        // Setup filter
        filter1 = new AlreadyParticipatedFilter(twitter, 1);
        filter2 = new AlreadyParticipatedFilter(twitter, 2);
    }

    @Test
    void test_simple_quote_retweet() {
        Status mockStatus = Mockito.mock(Status.class);
        when(mockStatus.getUser()).thenReturn(otherUser);
        when(mockStatus.getInReplyToStatusId()).thenReturn(0L);
        when(mockStatus.getText()).thenReturn("Haha goede grap! https://twitter.com/TwitterBot/status/123456789123456789");

        assertTrue(filter1.hasParticipatedInConversation(mockStatus));
        assertFalse(filter1.test(mockStatus));
    }

    @Test
    void test_quote_retweet_in_reply() throws TwitterException {
        Status quoteRetweet = Mockito.mock(Status.class);
        when(quoteRetweet.getUser()).thenReturn(otherUser);
        when(quoteRetweet.getInReplyToStatusId()).thenReturn(0L);
        when(quoteRetweet.getText()).thenReturn("Haha goede grap! https://twitter.com/TwitterBot/status/123456789123456789");

        Status replyToQuoteRetweet = Mockito.mock(Status.class);
        when(replyToQuoteRetweet.getUser()).thenReturn(otherUser2);
        when(replyToQuoteRetweet.getInReplyToStatusId()).thenReturn(5L);
        when(replyToQuoteRetweet.getText()).thenReturn("Inderdaad!");
        when(twitter.showStatus(5L)).thenReturn(quoteRetweet);


        assertFalse(filter1.hasParticipatedInConversation(replyToQuoteRetweet));
        assertTrue(filter2.hasParticipatedInConversation(replyToQuoteRetweet));
    }
}