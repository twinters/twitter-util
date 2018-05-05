package be.thomaswinters.twitter.userfetcher;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CachedUserFetcher  implements IUserFetcher {
    private final IUserFetcher innerFetcher;
    private final TemporalAmount allowedCachingTime;

    private List<Long> cache;
    private LocalDateTime lastCacheTime;


    public CachedUserFetcher(IUserFetcher innerFetcher, TemporalAmount allowedCachingTime) {
        this.innerFetcher = innerFetcher;
        this.allowedCachingTime = allowedCachingTime;
    }

    @Override
    public Stream<Long> fetchUserIds() {
        if (cache == null || LocalDateTime.now().isAfter(lastCacheTime.plus(allowedCachingTime))) {
            cache = innerFetcher.fetchUserIds().collect(Collectors.toList());
            lastCacheTime = LocalDateTime.now();
        }
        return cache.stream();

    }
}
