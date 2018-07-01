package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.*;
import com.beust.jcommander.IStringConverter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterBotModeConverter implements IStringConverter<ITwitterBotMode> {
    @Override
    public ITwitterBotMode convert(String value) {
        if (!value.contains(",")) {
            return parseMode(value);
        }
        return new CompositeMode(
                Stream.of(value.split(","))
                        .map(this::parseMode)
                        .collect(Collectors.toList()));
    }

    public ITwitterBotMode parseMode(String value) {

        switch (value) {
            case "post":
                return new PostMode();
            case "reply":
                return new ReplyMode();
            case "chat":
                return new ChatMode();
            default:
                throw new IllegalArgumentException("Not a valid Twitter bot mode: " + value);
        }
    }
}
