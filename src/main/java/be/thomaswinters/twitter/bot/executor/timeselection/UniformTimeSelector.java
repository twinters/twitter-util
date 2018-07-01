package be.thomaswinters.twitter.bot.executor.timeselection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UniformTimeSelector implements ITimeSelector {

    @Override
    public List<LocalDateTime> selectTimes(LocalDateTime start, TemporalAmount runDuration, int postTimes) {
        TemporalAmount timeInterval = Duration.ofSeconds(runDuration.get(ChronoUnit.SECONDS) / postTimes);

        return IntStream.range(0, postTimes)
                .mapToObj(i -> start.plusSeconds(((Duration) timeInterval).getSeconds() * i))
                .collect(Collectors.toList());
    }
}
