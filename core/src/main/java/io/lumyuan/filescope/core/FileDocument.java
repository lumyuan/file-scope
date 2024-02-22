package io.lumyuan.filescope.core;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;

import io.lumyuan.filescope.FileApplication;
import io.lumyuan.filescope.core.basic.FileBasic;
import io.lumyuan.filescope.util.DocumentUtil;
import io.lumyuan.filescope.util.UriUtil;

public class FileDocument extends FileBasic {

    private DocumentFile documentFile;

    protected FileDocument(String path) {
        super(path);
        DocumentFile treeDocumentFile = DocumentUtil.getTreeDocumentFile(path, false);
        if (treeDocumentFile == null) {
            treeDocumentFile = DocumentFile.fromTreeUri(FileApplication.getApplication(), UriUtil.path2DocumentFileUri(path));
        }
        documentFile = treeDocumentFile;
    }

    @Override
    public boolean exists() {
        return documentFile.exists();
    }

    @Override
    public boolean canRead() {
        return documentFile.canRead();
    }

    @Override
    public boolean canWrite() {
        return documentFile.canWrite();
    }

    @Override
    public boolean isDirectory() {
        return documentFile.isDirectory();
    }

    @Override
    public boolean isFile() {
        return documentFile.isFile();
    }

    @Override
    public long lastModified() {
        return documentFile.lastModified();
    }

    @Override
    public long length() {
        return documentFile.length();
    }

    @Override
    public boolean createNewFile() {
        DocumentFile file = DocumentUtil.getTreeDocumentFile(getParent(), true);
        assert file != null;
        file = file.createFile("*/*", getName());
        if (file != null){
            return file.exists();
        }
        return false;
    }

    @Override
    public boolean delete() {
        return documentFile.delete();
    }

    @Override
    public String[] list() {
        DocumentFile[] documentFiles = documentFile.listFiles();
        ArrayList<String> list = new ArrayList<>(documentFiles.length);
        for (DocumentFile file : documentFiles) {
            list.add(UriUtil.uri2path(file.getUri().toString()));
        }
        return list.toArray(new String[0]);
    }

    @Override
    public boolean mkdirs() {
        this.documentFile = DocumentUtil.getTreeDocumentFile(getPath(), true);
        if (documentFile != null) {
            return documentFile.exists();
        }
        return false;
    }

    @Override
    public boolean renameTo(String dest) {
        return documentFile.renameTo(dest);
    }
}
