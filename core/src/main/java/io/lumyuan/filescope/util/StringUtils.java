package io.lumyuan.filescope.util;

public class StringUtils {
    public static String trimSpace(String input) {
        int len = input.length();
        int st = 0;

        while ((st < len) && (input.charAt(st) <= ' ')) {
            st++;
        }
        while ((st < len) && (input.charAt(len - 1) <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < input.length())) ? input.substring(st, len) : input;
    }
}
