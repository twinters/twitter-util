package be.thomaswinters.twitter.bot.arguments;

import com.beust.jcommander.IStringConverter;

public class PostingModeConverter implements IStringConverter<PostingMode> {
    @Override
    public PostingMode convert(String value) {
        return PostingMode.valueOf(value.toUpperCase());
    }
}
