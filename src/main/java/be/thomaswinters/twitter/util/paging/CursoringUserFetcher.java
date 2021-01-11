package be.thomaswinters.twitter.util.paging;

import be.thomaswinters.twitter.exception.UncheckedTwitterException;
import org.apache.commons.lang3.ArrayUtils;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CursoringUserFetcher {
    private final Twitter twitter;
    private final Function<Long, IDs> fetcher;

    private final int LOOKUP_SIZE = 50;

    public CursoringUserFetcher(Twitter twitter, Function<Long, IDs> fetcher) {
        this.twitter = twitter;
        this.fetcher = fetcher;
    }

    public Stream<User> getUsers() {
        Cursorer cursorer = new Cursorer();
        Stream<List<User>> userStream = Stream.generate(cursorer);


        return userStream
                .takeWhile(list -> list.size() > 0)
                .flatMap(List::stream);
    }

    private List<User> lookup(Twitter twitter, IDs ids) throws TwitterException {
        List<User> result = new ArrayList<>();

        Long[] longObjects = ArrayUtils.toObject(ids.getIDs());
        List<Long> longList = java.util.Arrays.asList(longObjects);

        int from = 0;
        while (from <= longList.size()) {
            List<Long> sublist = longList.subList(from, Math.min(longList.size(), from + LOOKUP_SIZE));
            long[] primitiveSubList = sublist.stream().mapToLong(i -> i).toArray();
            result.addAll(twitter.lookupUsers(primitiveSubList));
            from += LOOKUP_SIZE;
        }

        return result;
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
            List<User> followers = new ArrayList<User>();
            try {
                followers = lookup(twitter, ids);
            } catch (TwitterException e) {
                e.printStackTrace();
                throw new UncheckedTwitterException(e);
            }
            done = !ids.hasNext();
            cursor = ids.getNextCursor();
            return followers;
        }

    }
}
