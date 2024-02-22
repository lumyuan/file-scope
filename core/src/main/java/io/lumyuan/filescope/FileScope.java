package io.lumyuan.filescope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.lumyuan.filescope.api.FilePermissionScope;
import io.lumyuan.filescope.core.IOFile;
import io.lumyuan.filescope.data.FileFramework;
import io.lumyuan.filescope.util.FilePermissionUtil;

public class FileScope {

    private static String SU = "su";

    private static FileFramework localFileFramework = FileFramework.NATIVE;

    public static String getSU() {
        return SU;
    }

    @NonNull
    public static FileFramework getLocalFileFramework() {
        return localFileFramework;
    }

    public static void setLocalFileFramework(FileFramework fileFramework) {
        FileScope.localFileFramework = fileFramework;
    }

    public static void filePermissionScope(@NonNull Context context, @NonNull String path, @Nullable ActivityResultLauncher<Intent> result, @NonNull FilePermissionScope scope) {
        final IOFile ioFile = new IOFile(context, path);
        if (localFileFramework == FileFramework.NATIVE) {
            if (ioFile.useSaf()) {
                if (FilePermissionUtil.hasAppSpecificDirectoryPermissions(path)) {
                    scope.scope(ioFile);
                } else {
                    if (result != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        result.launch(FilePermissionUtil.buildAppSpecificDirectoryPermissionIntent(path));
                    }
                }
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (FilePermissionUtil.hasManageAppAllFilesAccessPermission()) {
                    scope.scope(ioFile);
                }else {
                    FilePermissionUtil.requestManageAppAllFilesAccessPermission((Activity) context);
                }
            } else {
                if (FilePermissionUtil.hasExternalStoragePermission()) {
                    scope.scope(ioFile);
                }else  {
                    FilePermissionUtil.requestExternalStoragePermission((Activity) context);
                }
            }
        } else if (localFileFramework == FileFramework.SU) {
            if (ioFile.hasRoot()) {
                scope.scope(ioFile);
            } else {
                throw new RuntimeException("Root permission denied.");
            }
        } else {
            //TODO Shizuku预留
        }
    }

}
