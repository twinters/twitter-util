package be.thomaswinters.twitter.bot.executor.timeselection;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomTimeSelector implements ITimeSelector {

    @Override
    public List<LocalDateTime> selectTimes(LocalDateTime start, TemporalAmount runDuration, int postTimes) {
        long amountOfSeconds = runDuration.get(ChronoUnit.SECONDS);

        return IntStream
                .range(0,postTimes)
                .mapToObj(i -> {
                            if (amountOfSeconds > 0) {
                                return start.plusSeconds(ThreadLocalRandom.current().nextLong(amountOfSeconds));
                            } else {
                                return start;
                            }
                        }
                )
                .sorted()
                .collect(Collectors.toList());
    }
}
