package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.ITwitterBotMode;
import be.thomaswinters.twitter.bot.executor.modes.PostMode;
import be.thomaswinters.twitter.bot.executor.modes.ReplyMode;
import com.beust.jcommander.IStringConverter;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;

public class TemporalAmountConverter implements IStringConverter<TemporalAmount> {
    @Override
    public TemporalAmount convert(String value) {
        return Duration.parse("PT"+value.toUpperCase());
    }

}
