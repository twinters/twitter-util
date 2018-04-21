package be.thomaswinters.twitter.util.retriever.util;

import twitter4j.Paging;
import twitter4j.Status;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class PagingTweetDownloader implements Supplier<List<Status>> {

    private final Function<Paging, List<Status>> retriever;
    private Paging paging;

    public PagingTweetDownloader(Function<Paging, List<Status>> retriever, long since) {
        this.retriever = retriever;
        this.paging = new Paging(0, 100);
        paging.sinceId(since);
    }

    public PagingTweetDownloader

    public List<Status> get() {
        List<Status> tweets = retriever.apply(paging);
        paging.setPage(paging.getPage());
        return tweets;
    }
}
