package com.epic.epiccrawler.util;

public class LanguageUtilities {

    public static boolean containsNonEnglishCharacters(String input) {
        // Regular expression to match non-ASCII characters
        //String emojiRegex = "[\\u1F600-\\u1F64F\\u1F300-\\u1F5FF\\u1F680-\\u1F6FF]";
        //return input.matches(".*[^\\x00-\\x7F\\p{ASCII}\\p{Punct}\\p{Digit}" + emojiRegex + "].*");

        //return input.matches(".*[^\\p{ASCII}\\p{Punct}\\p{Digit}\\p{IsEmoji}].*");

        String pattern = "^[\\x00-\\x7F\\u1F600-\\u1F64F\\u1F300-\\u1F5FF\\u1F680-\\u1F6FF]*$";
        return input.matches(pattern);
    }


}
