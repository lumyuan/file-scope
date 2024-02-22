package io.lumyuan.filescope.util;

import java.text.DecimalFormat;

public class FileUtil {

    /**
     * 文件大小智能转换
     * 会将文件大小转换为最大满足单位
     * @param size（文件大小，单位为B）
     * @return 文件大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,###.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
