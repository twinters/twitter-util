package be.thomaswinters.twitter.util;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.stream.Collectors;


public class HashtagUtil {

    public static String hashtagify(String artist) {
        return Arrays.asList(WordUtils.capitalizeFully(artist).split("&")).stream()
                .map(e -> "#" + e.replaceAll(" ", "").replaceAll("[^\\w+]", "")).collect(Collectors.joining(" "));
    }
}
