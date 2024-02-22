package io.lumyuan.filescope.core.shell;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.lumyuan.filescope.data.FileFramework;

public class KeepShellPublic {

    private static final Map<Object, KeepShell> keepShells = new HashMap<>();

    public static KeepShell getInstance(Object key, FileFramework framework) {
        synchronized (keepShells) {
            if (!keepShells.containsKey(key)) {
                keepShells.put(key, new KeepShell(framework));
            }
            return keepShells.get(key);
        }
    }

    public static void destroyInstance(Object key) {
        synchronized (keepShells) {
            if (keepShells.containsKey(key)) {
                KeepShell keepShell = keepShells.get(key);
                keepShells.remove(key);
                if (keepShell != null) {
                    keepShell.tryExit();
                }
            }
        }
    }

    public static void destroyAll() {
        synchronized (keepShells) {
            if (!keepShells.isEmpty()) {
                Set<Map.Entry<Object, KeepShell>> entries = keepShells.entrySet();
                for (Map.Entry<Object, KeepShell> entry : entries) {
                    entry.getValue().tryExit();
                }
                keepShells.clear();
            }
        }
    }

    public static KeepShell defaultKeepShell = new KeepShell(FileFramework.SU);
    public static KeepShell secondaryKeepShell = new KeepShell(FileFramework.SU);

    @NonNull
    public static KeepShell getDefaultInstance() {
        if (defaultKeepShell.isIdle() || !secondaryKeepShell.isIdle()) {
            return defaultKeepShell;
        } else {
            return secondaryKeepShell;
        }
    }

    public static String doCmdSync(String cmd) {
        return getDefaultInstance().doCmdSync(cmd);
    }

    public static boolean checkRoot() {
        return defaultKeepShell.checkRoot();
    }

    public static void tryExit() {
        defaultKeepShell.tryExit();
        secondaryKeepShell.tryExit();
    }

}
