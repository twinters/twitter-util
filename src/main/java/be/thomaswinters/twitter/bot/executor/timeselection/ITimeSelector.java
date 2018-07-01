package be.thomaswinters.twitter.bot.executor.timeselection;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

public interface ITimeSelector {
    List<LocalDateTime> selectTimes(LocalDateTime start, TemporalAmount runDuration, int postTimes);
}
