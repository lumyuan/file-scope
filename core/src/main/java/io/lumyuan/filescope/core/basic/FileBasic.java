package io.lumyuan.filescope.core.basic;

import java.io.File;
import java.io.IOException;

public abstract class FileBasic {

    protected final File javaFile;

    protected FileBasic(String path) {
        this.javaFile = new File(path);
    }

    public abstract boolean exists();

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname.  This is just the last name in the pathname's name
     * sequence.  If the pathname's name sequence is empty, then the empty
     * string is returned.
     *
     * @return  The name of the file or directory denoted by this abstract
     *          pathname, or the empty string if this pathname's name sequence
     *          is empty
     */
    public String getName() {
        return javaFile.getName();
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     *
     * @return  The pathname string of the parent directory named by this
     *          abstract pathname, or <code>null</code> if this pathname
     *          does not name a parent
     */
    public String getParent() {
        return javaFile.getParent();
    }

    /**
     * Converts this abstract pathname into a pathname string.
     *
     * @return  The string form of this abstract pathname
     */
    public String getPath() {
        return javaFile.getPath();
    }

    public abstract boolean canRead();

    public abstract boolean canWrite();

    public abstract boolean isDirectory();

    public abstract boolean isFile();

    public abstract long lastModified();

    public abstract long length();

    public abstract boolean createNewFile() throws IOException;

    public abstract boolean delete();

    public abstract String[] list();

    public abstract boolean mkdirs();

    public abstract boolean renameTo(String dest);
}
