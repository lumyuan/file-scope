package io.lumyuan.filescope.util;

import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;

import io.lumyuan.filescope.FileApplication;
import io.lumyuan.filescope.common.Documents;

public class UriUtil {

    /**
     * Convert absolute path to Android Document File Uri.
     * @param path absolute path
     * @return android Uri
     */
    public static Uri path2DocumentFileUri(String path){
        final String reallyPath = new File(path).getAbsolutePath();
        final String primaryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String dataPath = reallyPath.substring(reallyPath.indexOf(primaryPath) + primaryPath.length() + 1);
        final Uri uri = DocumentsContract.buildTreeDocumentUri(Documents.EXTERNAL_STORAGE_PROVIDER_AUTHORITY, Documents.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID + ":" + dataPath);
        final DocumentFile documentFile = DocumentFile.fromTreeUri(FileApplication.getApplication(), uri);
        assert documentFile != null;
        return documentFile.getUri();
    }

    /**
     * Convert absolute path to Android Uri.
     * @param path absolute path
     * @return android Uri
     */
    public static Uri path2Uri(String path){
        final String reallyPath = new File(path).getAbsolutePath();
        final String primaryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String dataPath = reallyPath.substring(reallyPath.indexOf(primaryPath) + primaryPath.length() + 1);
        return DocumentsContract.buildTreeDocumentUri(Documents.EXTERNAL_STORAGE_PROVIDER_AUTHORITY, Documents.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID + ":" + dataPath);
    }

    /**
     * Convert Android Content Uri to path.
     * @param uri android Uri
     * @return absolutePath
     */
    public static String uri2path(String uri){
        final String uriBody = uri.substring(0, uri.lastIndexOf("%3A") + 3);
        String uriContent = uri.replaceAll(uriBody, "");
        return new File(Environment.getExternalStorageDirectory(), Uri.decode(uriContent)).getAbsolutePath();
    }
}
