package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.*;
import be.thomaswinters.twitter.bot.executor.timeselection.ITimeSelector;
import be.thomaswinters.twitter.bot.executor.timeselection.RandomTimeSelector;
import be.thomaswinters.twitter.bot.executor.timeselection.UniformTimeSelector;
import com.beust.jcommander.IStringConverter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSelectorConverter implements IStringConverter<ITimeSelector> {
    @Override
    public ITimeSelector convert(String value) {
        switch (value) {
            case "uniform":
                return new UniformTimeSelector();
            case "random":
                return new RandomTimeSelector();
            default:
                throw new IllegalArgumentException("Not a valid time selector: " + value);
        }
    }

}
