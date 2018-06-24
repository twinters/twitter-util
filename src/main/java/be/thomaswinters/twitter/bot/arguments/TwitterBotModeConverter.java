package be.thomaswinters.twitter.bot.arguments;

import be.thomaswinters.twitter.bot.executor.modes.ITwitterBotMode;
import be.thomaswinters.twitter.bot.executor.modes.ReplyMode;
import be.thomaswinters.twitter.bot.executor.modes.PostMode;
import com.beust.jcommander.IStringConverter;

public class TwitterBotModeConverter implements IStringConverter<ITwitterBotMode> {
    @Override
    public ITwitterBotMode convert(String value) {
        switch (value) {
            case "post":
                return new PostMode();
            case "reply":
                return new ReplyMode();
            default:
                throw new IllegalArgumentException("Not a valid posting mode: " + value);
        }
    }
}
