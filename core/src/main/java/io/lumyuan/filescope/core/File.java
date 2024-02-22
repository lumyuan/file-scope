package io.lumyuan.filescope.core;

import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;

import io.lumyuan.filescope.FileScope;
import io.lumyuan.filescope.core.basic.FileBasic;
import io.lumyuan.filescope.data.FileFramework;

public class File extends FileBasic {

    private final FileFramework fileFramework;
    private FileDocument fileDocument;
    private FileRoot fileRoot;

    public File(String path) {
        super(path);
        this.fileFramework = FileScope.getLocalFileFramework();
        switch (fileFramework) {
            case SU:
                fileRoot = new FileRoot(path);
                break;
            case NATIVE:
                if (useSaf()) {
                    fileDocument = new FileDocument(path);
                }
                break;
        }
    }

    public boolean useSaf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String parent = getParent();
            if (parent == null) {
                return false;
            } else {
                return parent.startsWith("/sdcard/Android/") || parent.startsWith("/storage/self/primary/Android/") || parent.startsWith("/storage/emulated/0/Android/")/*parent.matches("^/storage/emulated/[^/]+/Android(/[^/]*)*$")*/;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean exists() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.exists();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.exists();
        } else {
            return javaFile.exists();
        }
    }

    @Override
    public boolean canRead() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.canRead();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.canRead();
        } else {
            return javaFile.canRead();
        }
    }

    @Override
    public boolean canWrite() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.canWrite();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.canWrite();
        } else {
            return javaFile.canWrite();
        }
    }

    @Override
    public boolean isDirectory() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.isDirectory();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.isDirectory();
        } else {
            return javaFile.isDirectory();
        }
    }

    @Override
    public boolean isFile() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.isFile();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.isFile();
        } else {
            return javaFile.isFile();
        }
    }

    @Override
    public long lastModified() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.lastModified();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.lastModified();
        } else {
            return javaFile.lastModified();
        }
    }

    @Override
    public long length() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.length();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.length();
        } else {
            return javaFile.length();
        }
    }

    @Override
    public boolean createNewFile() throws IOException {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.createNewFile();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.createNewFile();
        } else {
            return javaFile.createNewFile();
        }
    }

    @Override
    public boolean delete() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.delete();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.delete();
        } else {
            return javaFile.delete();
        }
    }

    @Override
    public String[] list() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.list();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.list();
        } else {
            String path = getPath();
            String[] ss = javaFile.list();
            if (ss == null) return null;
            int n = ss.length;
            String[] ps = new String[n];
            for (int i = 0; i < n; i++) {
                ps[i] = path + "/" + ss[i];
            }
            return ps;
        }
    }

    public File[] listFiles() {
        String[] ss = list();
        if (ss == null) return null;
        int n = ss.length;
        File[] fs = new File[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new File(ss[i]);
        }
        return fs;
    }

    @Override
    public boolean mkdirs() {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.mkdirs();
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.mkdirs();
        } else {
            return javaFile.mkdirs();
        }
    }

    @Override
    public boolean renameTo(String dest) {
        if (fileFramework == FileFramework.SU) {
            return fileRoot.renameTo(dest);
        }
        if (fileFramework == FileFramework.SHIZUKU) {

        }
        if (useSaf()) {
            return fileDocument.renameTo(dest);
        } else {
            return javaFile.renameTo(new java.io.File(getParent(), dest));
        }
    }
}
