package be.thomaswinters.twitter.util.download;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import be.thomaswinters.twitter.util.scouter.ITweetScouter;
import be.thomaswinters.twitter.util.scouter.UserScouter;
import twitter4j.Status;
import twitter4j.TwitterException;

public class TweetDownloader {
	private ITweetScouter scouter;

	public TweetDownloader(ITweetScouter scouter) {
		this.scouter = scouter;
	}

	public String downloadTo(File file) throws IOException, TwitterException {
		Collection<Status> tweets = scouter.scout();
		String lines = tweets.stream().map(e -> e.getText()).collect(Collectors.joining("\n"));
		Files.write(lines, file, Charsets.UTF_16);
		return lines;
	}

	public static void main(String[] args) throws IOException, TwitterException {
		String user = args[0];
		(new TweetDownloader(new UserScouter(user))).downloadTo(new File("res/" + user + ".txt"));
	}

}
