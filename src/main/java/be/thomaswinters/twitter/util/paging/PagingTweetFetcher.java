package be.thomaswinters.twitter.util.paging;

import twitter4j.Paging;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PagingTweetFetcher {

    private final Function<Paging, List<Status>> retriever;

    public PagingTweetFetcher(Function<Paging, List<Status>> retriever) {
        this.retriever = retriever;
    }

    public Stream<Status> getTweets(long sinceId) {
        Pager pager = new Pager(sinceId);
        return Stream.generate(pager)
                .takeWhile(list -> list.size() > 0)
                .flatMap(List::stream);
    }

    private class Pager implements Supplier<List<Status>> {

        private Paging paging;
        private boolean done;

        public Pager(long sinceId) {
            this.paging = new Paging(1, 100);
            paging.sinceId(sinceId);
        }

        public List<Status> get() {
            if (done) {
                return new ArrayList<>();
            }
            List<Status> tweets = retriever.apply(paging);
            done = tweets.isEmpty();
            paging.setPage(paging.getPage() + 1);
            return tweets;
        }

    }

}
