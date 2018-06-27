package be.thomaswinters.twitter.bot;

import be.thomaswinters.twitter.exception.ExcessiveTweetLengthException;
import be.thomaswinters.twitter.util.TwitterUtil;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A class used to perform several Twitter actions that others can listen to
 */
public class Tweeter implements ITweeter {

    private final Twitter twitterConnection;

    private final Collection<Consumer<User>> followListener = new ArrayList<>();
    private final Collection<Consumer<Status>> postListeners = new ArrayList<>();
    private final Collection<BiConsumer<Status, Status>> replyListeners = new ArrayList<>();

    public Tweeter(Twitter twitterConnection) {
        this.twitterConnection = twitterConnection;
    }

    //region Performers
    @Override
    public Status quoteRetweet(String status, Status toTweet) throws TwitterException {
        return tweet(status + " " + TwitterUtil.getQuoteRetweetUrl(toTweet));
    }


    @Override
    public Status tweet(String status) throws TwitterException {
        Status post = twitterConnection.updateStatus(status);
        notifyNewPostListeners(post);
        return post;
    }

    @Override
    public Status reply(String replyText, Status toTweet) throws TwitterException {
        String fullReplyText = "@" + toTweet.getUser().getScreenName() + " " + replyText;

        if (!TwitterUtil.hasValidLength(fullReplyText)) {
            throw new ExcessiveTweetLengthException(fullReplyText);
        }
        StatusUpdate replyPreparation = new StatusUpdate(fullReplyText);
        replyPreparation.inReplyToStatusId(toTweet.getId());
        Status post = twitterConnection.updateStatus(replyPreparation);
        notifyNewReplyListeners(post, toTweet);
        return post;
    }


    @Override
    public void follow(User user) throws TwitterException {
        twitterConnection.createFriendship(user.getId());
        notifyNewFollowListeners(user);
    }

    @Override
    public Twitter getTwitterConnection() {
        return twitterConnection;
    }
    //endregion

    //region Listeners
    public void addPostListener(Consumer<Status> listener) {
        this.postListeners.add(listener);
    }

    public void removePostListener(Consumer<Status> listener) {
        this.postListeners.remove(listener);
    }

    private void notifyNewPostListeners(Status post) {
        postListeners.forEach(f -> f.accept(post));
    }

    public void addReplyListener(BiConsumer<Status, Status> listener) {
        this.replyListeners.add(listener);
    }

    public void removeReplyListener(BiConsumer<Status, Status> listener) {
        this.replyListeners.remove(listener);
    }

    private void notifyNewReplyListeners(Status reply, Status toTweet) {
        replyListeners.forEach(f -> f.accept(reply, toTweet));
    }

    public void addFollowListener(Consumer<User> listener) {
        this.followListener.add(listener);
    }

    public void removeFollowListener(Consumer<User> listener) {
        this.followListener.remove(listener);
    }

    private void notifyNewFollowListeners(User user) {
        followListener.forEach(f -> f.accept(user));
    }

    //endregion
}
