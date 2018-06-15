package be.thomaswinters.twitter.util.download;

import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.UserTweetsFetcher;
import be.thomaswinters.twitter.util.TwitterLogin;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetDownloader {
    private boolean mapLineBreaksToSpaces = true;
    private ITweetsFetcher scouter;

    public TweetDownloader(ITweetsFetcher scouter) {
        this.scouter = scouter;
    }

    public static void main(String[] args) throws IOException, TwitterException {
        String user = args[0];
        (new TweetDownloader(
                new UserTweetsFetcher(
                        TwitterLogin.getTwitterFromEnvironment(), user, false, true)))
                .downloadTo(
                        new File(user + ".txt"));
    }

    public void downloadTo(File file) throws IOException, TwitterException {
        Stream<Status> tweets = scouter.retrieve();
        String lines = tweets.map(Status::getText)
                .map(e -> mapLineBreaksToSpaces ? e.replaceAll("\n", " ") : e)
                .collect(Collectors.joining("\n"));
        Files.write(lines, file, Charsets.UTF_8);
    }

}
