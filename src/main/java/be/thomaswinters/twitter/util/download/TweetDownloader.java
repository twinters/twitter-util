package be.thomaswinters.twitter.util.download;

import be.thomaswinters.twitter.bot.util.TwitterLoginUtil;
import be.thomaswinters.twitter.tweetsfetcher.ITweetsFetcher;
import be.thomaswinters.twitter.tweetsfetcher.UserTweetsFetcher;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetDownloader {
    private ITweetsFetcher scouter;

    public TweetDownloader(ITweetsFetcher scouter) {
        this.scouter = scouter;
    }

    private void downloadTo(File file) throws IOException, TwitterException {
        Stream<Status> tweets = scouter.retrieve();
        String lines = tweets.map(Status::getText).collect(Collectors.joining("\n"));
        Files.write(lines, file, Charsets.UTF_16);
    }

    public static void main(String[] args) throws IOException, TwitterException {
        String user = args[0];
        (new TweetDownloader(
                new UserTweetsFetcher(
                        TwitterLoginUtil.getTwitterFromEnvironment(), user, false, false)))
                .downloadTo(
                        new File(user + ".txt"));
    }

}
