package be.thomaswinters.twitter.util.analysis;

import com.google.common.collect.ImmutableList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterTrendsFinder {

    /*-********************************************-*
     *  Cache
     *-********************************************-*/
    private static int REFETCH_TRENDS_MINUTES = 60;
    private static Map<Integer, TrendsCache> cache = new HashMap<>();

    public static class TrendsCache {
        private final LocalDateTime fetched;
        private final ImmutableList<String> trends;

        public TrendsCache(LocalDateTime fetched, Collection<? extends String> trends) {
            this.fetched = fetched;
            this.trends = ImmutableList.copyOf(trends);
        }

        public LocalDateTime getFetchedDate() {
            return fetched;
        }

        public ImmutableList<String> getTrends() {
            return trends;
        }
    }

    /*-********************************************-*/

    public static List<String> getCurrentTrends(int location) throws TwitterException {

        if (cache.containsKey(location) && cache.get(location).getFetchedDate().plusMinutes(REFETCH_TRENDS_MINUTES)
                .isAfter(LocalDateTime.now())) {
            return cache.get(location).getTrends();
        }

//		System.out.println("Fetching trends in " + location);
        Twitter twitter = TwitterFactory.getSingleton();
        List<String> trends = Stream.of(twitter.trends().getPlaceTrends(location).getTrends())
                .map(e -> e.getName()).collect(Collectors.toList());

        cache.put(location, new TrendsCache(LocalDateTime.now(), trends));

        System.out.println(trends);

        return trends;
    }

    public static List<String> getCurrentNonHashtagTrends(int location) throws TwitterException {
        return getCurrentTrends(location).stream().filter(e -> !e.startsWith("#")).collect(Collectors.toList());
    }

//	public static Collection<String> getTrendingPeople(int location) throws TwitterException {
//
//		List<String> trends = getCurrentNonHashtagTrends(location);
//
//		// Order is important, but not duplicates
//		Collection<String> trendingPeople = new LinkedHashSet<>();
//
//		for (String trend : trends) {
//			try {
//				if (!DutchNamedEntityRecogniser.findNames(trend).isEmpty()) {
//					trendingPeople.add(trend);
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return trendingPeople;
//
//	}

}
