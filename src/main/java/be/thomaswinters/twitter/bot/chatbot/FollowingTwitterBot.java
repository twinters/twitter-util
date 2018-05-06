package be.thomaswinters.twitter.bot.chatbot;

import be.thomaswinters.twitter.util.analysis.FollowerChecker;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.Optional;

public class FollowingTwitterBot implements ITwitterChatBot {
    private final Twitter twitter;
    private final FollowerChecker followerUtil;

    public FollowingTwitterBot(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.followerUtil = new FollowerChecker(twitter);
    }

    @Override
    public Optional<String> generateReply(Status tweet) {
        User user = tweet.getUser();
        if (!followerUtil.isFollowing(user)) {
            try {
                followerUtil.follow(user);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
