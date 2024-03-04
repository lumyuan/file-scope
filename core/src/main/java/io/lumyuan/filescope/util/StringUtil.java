package io.lumyuan.filescope.util;

public class StringUtil {
    public static String getReallyPath(String absolutePath){
        if (absolutePath.lastIndexOf("/") == absolutePath.length() - 1){
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        }
        return absolutePath;
    }

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
