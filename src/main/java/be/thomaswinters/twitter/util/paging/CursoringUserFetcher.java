package be.thomaswinters.twitter.util.paging;

import org.apache.commons.lang3.ArrayUtils;
import twitter4j.IDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CursoringUserFetcher {

    private final Function<Long, IDs> fetcher;

    public CursoringUserFetcher(Function<Long, IDs> fetcher) {
        this.fetcher = fetcher;
    }

    public Stream<Long> getUsers() {
        Cursorer pager = new Cursorer();
        return Stream.generate(pager)
                .takeWhile(list -> list.size() > 0)
                .flatMap(List::stream);
    }

    private class Cursorer implements Supplier<List<Long>> {

        private Long cursor;
        private boolean done;

        public Cursorer() {
            this.cursor = 0L;
        }

        public List<Long> get() {
            if (done) {
                return new ArrayList<>();
            }
            IDs tweets = fetcher.apply(cursor);
            done = tweets.hasNext();
            cursor = tweets.getNextCursor();
            return Arrays.asList(ArrayUtils.toObject(tweets.getIDs()));
        }

    }

}
