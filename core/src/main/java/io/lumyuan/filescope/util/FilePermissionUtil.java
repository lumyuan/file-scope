package io.lumyuan.filescope.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;

import io.lumyuan.filescope.FileApplication;

public class FilePermissionUtil {

    public static boolean hasAppSpecificDirectoryPermissions(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<UriPermission> persistedUriPermissions = FileApplication.getApplication().getContentResolver().getPersistedUriPermissions();
            for (UriPermission persistedUriPermission : persistedUriPermissions) {
                String pathUri = UriUtil.path2Uri(DocumentUtil.getRootPath(path)).toString();
                String uri = persistedUriPermission.getUri().toString();
                if (pathUri.equals(uri) && (persistedUriPermission.isReadPermission() || persistedUriPermission.isWritePermission())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    public static Intent buildAppSpecificDirectoryPermissionIntent(@NonNull String path) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, UriUtil.path2DocumentFileUri(DocumentUtil.getRootPath(path)));
        return intent;
    }

    @SuppressLint("WrongConstant")
    public static void requestAppSpecificDirectoryPermissionResultLauncher(@NonNull Activity activity, @NonNull ActivityResult result) {
        Intent data = result.getData();
        if (data != null && result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            }
        }
    }

    public static void requestManageAppAllFilesAccessPermission(Activity activity) {
        if (!hasManageAppAllFilesAccessPermission()) {
            Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
                    .setData(Uri.parse("package:" + activity.getPackageName()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    public static boolean hasManageAppAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }
        return true;
    }

    public static boolean hasExternalStoragePermission(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(FileApplication.getApplication(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static final int REQUEST_PERMISSION_CODE = 20240221;
    public static void requestExternalStoragePermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_PERMISSION_CODE);
    }
}
