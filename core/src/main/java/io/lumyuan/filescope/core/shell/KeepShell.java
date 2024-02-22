package io.lumyuan.filescope.core.shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import io.lumyuan.filescope.data.FileFramework;

public class KeepShell {

    private Process p;
    private OutputStream out;
    private BufferedReader reader;
    /**
     * 是否处于闲置状态
     */
    private boolean currentIsIdle = true;
    private boolean isIdle = true;

    public boolean isIdle() {
        isIdle = currentIsIdle;
        return isIdle;
    }

    private void setIdle(boolean isIdle) {
        this.isIdle = isIdle;
    }

    private FileFramework fileFramework;

    public KeepShell(FileFramework fileFramework) {
        this.fileFramework = fileFramework;
    }

    public KeepShell() {
        this(FileFramework.SU);
    }

    public void tryExit() {
        try {
            if (out != null)
                out.close();
            if (reader != null)
                reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        enterLockTime = 0L;
        out = null;
        reader = null;
        p = null;
        currentIsIdle = true;
    }

    //获取ROOT超时时间
    private Long GET_ROOT_TIMEOUT = 20000L;
    private ReentrantLock mLock = new ReentrantLock();
    private Long LOCK_TIMEOUT = 10000L;
    private Long enterLockTime = 0L;

    private final String checkRootState =
            // "if [[ $(id -u 2>&1) == '0' ]] || [[ $($UID) == '0' ]] || [[ $(whoami 2>&1) == 'root' ]] || [[ $($USER_ID) == '0' ]]; then\n" +
            "if [[ $(id -u 2>&1) == '0' ]] || [[ $($UID) == '0' ]] || [[ $(whoami 2>&1) == 'root' ]] || [[ $(set | grep 'USER_ID=0') == 'USER_ID=0' ]]; then\n" +
                    "  echo 'success'\n" +
                    "else\n" +
                    "if [[ -d /cache ]]; then\n" +
                    "  echo 1 > /cache/filescope_root\n" +
                    "  if [[ -f /cache/filescope_root ]] && [[ $(cat /cache/filescope_root) == '1' ]]; then\n" +
                    "    echo 'success'\n" +
                    "    rm -rf /cache/filescope_root\n" +
                    "    return\n" +
                    "  fi\n" +
                    "fi\n" +
                    "exit 1\n" +
                    "exit 1\n" +
                    "fi\n";

    public boolean checkRoot() {
        String r = doCmdSync(checkRootState).toLowerCase(Locale.getDefault());
        if (r.equals("error") || r.contains("permission denied") || r.contains("not allowed") || r.equals("not found")) {
            if (fileFramework == FileFramework.SU) {
                tryExit();
            }
            return false;
        } else if (r.contains("success")) {
            return true;
        } else {
            if (fileFramework == FileFramework.SU) {
                tryExit();
            }
            return false;
        }
    }

    private void getRuntimeShell() throws InterruptedException {
        if (p != null) return;
        Thread getSu = new Thread(() -> {
            try {
                mLock.lockInterruptibly();
                enterLockTime = System.currentTimeMillis();
                switch (fileFramework) {
                    case SU:
                        p = ShellExecutor.getSuperUserRuntime();
                        break;
                    case NATIVE:
                        p = ShellExecutor.getRuntime();
                    case SHIZUKU:
                        p = ShellExecutor.getRuntime();
                        break;
                }
                assert p != null;
                out = p.getOutputStream();
                reader = new BufferedReader(new InputStreamReader((p.getInputStream())));
                if (fileFramework == FileFramework.SU && out != null) {
                    out.write(checkRootState.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                new Thread(() -> {
                    try {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        while (true) {
                            Log.e("KeepShellPublic", errorReader.readLine());
                        }
                    } catch (Exception e) {
                        Log.e("c", "" + e.getMessage());
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                enterLockTime = 0L;
                mLock.unlock();
            }
        });
        getSu.start();
        getSu.join(10000);
        if (p == null && getSu.getState() != Thread.State.TERMINATED) {
            enterLockTime = 0L;
            getSu.interrupt();
        }
    }

    private StringBuilder shellOutputCache = new StringBuilder();
    private String startTag = "|SH>>|";
    private String endTag = "|<<SH|";
    private byte[] startTagBytes = ("\necho '" + startTag + "'\n").getBytes(StandardCharsets.UTF_8);
    private byte[] endTagBytes = ("\necho '" + endTag + "'\n").getBytes(StandardCharsets.UTF_8);

    public String doCmdSync(String cmd) {
        if (mLock.isLocked() && enterLockTime > 0 && System.currentTimeMillis() - enterLockTime > LOCK_TIMEOUT) {
            tryExit();
            Log.e("doCmdSync-Lock", "线程等待超时${System.currentTimeMillis()} - $enterLockTime > $LOCK_TIMEOUT");
        }
        try {
            getRuntimeShell();
            mLock.lockInterruptibly();
            currentIsIdle = false;

            assert out != null;
            out.write(startTagBytes);
            out.write(cmd.getBytes(StandardCharsets.UTF_8));
            out.write(endTagBytes);
            out.flush();

            boolean unStart = true;
            while (reader != null) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else if (line.contains(endTag)) {
                    shellOutputCache.append(line.substring(0, line.indexOf(endTag)));
                    break;
                } else if (line.contains(startTag)) {
                    shellOutputCache = new StringBuilder();
                    shellOutputCache.append(line.substring(line.indexOf(startTag) + startTag.length()));
                    unStart = false;
                } else if (!unStart) {
                    shellOutputCache.append(line);
                    shellOutputCache.append("\n");
                }
            }
            // Log.e("shell-unlock", cmd)
            // Log.d("Shell", cmd.toString() + "\n" + "Result:"+results.toString().trim())
            return shellOutputCache.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            tryExit();
            return "error";
        } finally {
            enterLockTime = 0L;
            mLock.unlock();

            currentIsIdle = true;
        }
    }

}
