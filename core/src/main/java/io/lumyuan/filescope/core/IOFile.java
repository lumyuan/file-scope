package io.lumyuan.filescope.core;

import android.content.Context;

import io.lumyuan.filescope.core.shell.KeepShellPublic;

public final class IOFile extends File {

    private final Context context;

    public IOFile(Context context, String path) {
        super(path);
        this.context = context;
    }

    public boolean hasRoot() {
        return KeepShellPublic.checkRoot();
    }

}
