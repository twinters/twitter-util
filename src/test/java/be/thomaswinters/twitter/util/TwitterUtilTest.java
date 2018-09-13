package be.thomaswinters.twitter.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwitterUtilTest {

    @Test
    public void removeTwitterWords_test() {
        assertEquals("These are some words for about the hashtag and such?",
                TwitterUtil.removeTwitterWords("These are some words for @user about the hashtag #popular and such? \n" +
                        "#wow #nice"));
    }

}