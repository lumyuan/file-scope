package io.lumyuan.filescope.core.shell;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import io.lumyuan.filescope.FileScope;
import io.lumyuan.filescope.util.StringUtil;

public class ShellExecutor {
    private static String extraEnvPath = "";
    private static String defaultEnvPath = ""; // /sbin:/system/sbin:/system/bin:/system/xbin:/odm/bin:/vendor/bin:/vendor/xbin

    public static void setExtraEnvPath(String extraEnvPath) {
        ShellExecutor.extraEnvPath = extraEnvPath;
    }

    @Nullable
    private static String getEnvPath() {
        // FIXME:非root模式下，默认的 TMPDIR=/data/local/tmp 变量可能会导致某些需要写缓存的场景（例如使用source指令）脚本执行失败！
        if (extraEnvPath != null && !TextUtils.isEmpty(extraEnvPath) ) {
            if (TextUtils.isEmpty(defaultEnvPath)) {
                try {
                    Process process = Runtime.getRuntime().exec("sh");
                    OutputStream outputStream = process.getOutputStream();
                    outputStream.write("echo $PATH".getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    outputStream.close();
                    InputStream inputStream = process.getInputStream();
                    byte[] cache = new byte[16384];
                    int length = inputStream.read(cache);
                    inputStream.close();
                    process.destroy();
                    String path = StringUtil.trimSpace(new String(cache, 0, length));
                    if (!path.isEmpty()) {
                        defaultEnvPath =  path;
                    } else {
                        throw new RuntimeException("未能获取到$PATH参数");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    defaultEnvPath = "/sbin:/system/sbin:/system/bin:/system/xbin:/odm/bin:/vendor/bin:/vendor/xbin";
                }
                return "PATH=" + defaultEnvPath + ":" + extraEnvPath;
            }

        }
        return null;
    }

    @Nullable
    private static Process getProcess(String run) throws IOException {
        String env = getEnvPath();
        Runtime runtime = Runtime.getRuntime();
        /*
        // 部分机型会有Aborted错误
        if (env != null) {
            return runtime.exec(run, new String[]{
                env
            });
        }
        */
        Process process = runtime.exec(run);
        if (env != null) {
            OutputStream outputStream = process.getOutputStream();
            outputStream.write("export ".getBytes(StandardCharsets.UTF_8));
            outputStream.write(env.getBytes(StandardCharsets.UTF_8));
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        return process;
    }

    @Nullable
    public static Process getSuperUserRuntime() throws IOException {
        return getProcess(FileScope.getSU());
    }

    @Nullable
    public static Process getRuntime() throws IOException {
        return getProcess("sh");
    }

}
