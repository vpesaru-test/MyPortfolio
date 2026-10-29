package com.vidyullatha.portfolio.support;

import java.util.List;
import java.util.stream.Collectors;

/** Text helpers so comparisons ignore line breaks and repeated spaces from HTML layout. */
public final class Text {

    private Text() {
    }

    public static String norm(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(' ', ' ').replaceAll("\\s+", " ").trim();
    }

    public static List<String> norm(List<String> texts) {
        return texts.stream().map(Text::norm).collect(Collectors.toList());
    }

    public static String digitsOnly(String text) {
        return text == null ? "" : text.replaceAll("\\D", "");
    }
}
