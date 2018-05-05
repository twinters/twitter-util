package be.thomaswinters.twitter.util.paging;

import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CursoringUserFetcher {
    private final Twitter twitter;
    private final Function<Long, IDs> fetcher;

    public CursoringUserFetcher(Twitter twitter, Function<Long, IDs> fetcher) {
        this.twitter = twitter;
        this.fetcher = fetcher;
    }

    public Stream<User> getUsers() {
        Cursorer cursorer = new Cursorer();
        return Stream.generate(cursorer)
                .takeWhile(list -> list.size() > 0)
                .flatMap(List::stream);
    }

    private class Cursorer implements Supplier<List<User>> {

        private long cursor = -1;
        private IDs ids;
        private boolean done;

        public List<User> get() {
            if (done) {
                return new ArrayList<>();
            }
            ids = fetcher.apply(cursor);
            try {
                ResponseList<User> followers = twitter.lookupUsers(ids.getIDs());
                done = !ids.hasNext();
                cursor = ids.getNextCursor();
                return followers;
            } catch (TwitterException e) {
                e.printStackTrace();
                throw new UncheckedTwitterException(e);
            }
        }

    }
}
