package io.lumyuan.filescope.util;

public class StringUtil {
    public static String getReallyPath(String absolutePath){
        if (absolutePath.lastIndexOf("/") == absolutePath.length() - 1){
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        }
        return absolutePath;
    }
}
