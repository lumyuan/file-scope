package io.lumyuan.filescope.util;

import android.os.Build;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;

import io.lumyuan.filescope.FileApplication;

public class DocumentUtil {

    private static String getReallyPath(String path) {
        return new File(path).getAbsolutePath();
    }

    public static String getRootPath(String path) {
        path = getReallyPath(path);
        String[] split = path.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ? 7 : 8); i++) {
            builder.append("/").append(split[i]);
        }
        return builder.toString();
    }

    public static String getPathContent(String absolutePath){
        String reallyPath = getRootPath(absolutePath);
        absolutePath = absolutePath.substring(absolutePath.indexOf(reallyPath) + reallyPath.length());
        return absolutePath.startsWith("/") ? absolutePath.substring(1) : absolutePath;
    }

    public static DocumentFile getTreeDocumentFile(String absolutePath, boolean isCreateDir){
        String reallyPath = getReallyPath(absolutePath);
        final String pathContent = getPathContent(reallyPath);
        String[] fileItems = pathContent.split("/");
        DocumentFile documentFile = DocumentFile.fromTreeUri(FileApplication.getApplication(), UriUtil.path2Uri(getRootPath(reallyPath)));
        for (String item : fileItems) {
            if (documentFile == null) {
                return null;
            }
            final DocumentFile file = documentFile.findFile(item);
            if (file != null) {
                documentFile = file;
            } else if (isCreateDir) {
                documentFile = documentFile.createFile("*/*", item);
            } else {
                return null;
            }
        }
        return documentFile;
    }
}
