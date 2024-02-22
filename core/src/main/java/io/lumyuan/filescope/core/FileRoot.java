package io.lumyuan.filescope.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.lumyuan.filescope.core.basic.FileBasic;
import io.lumyuan.filescope.core.shell.KeepShellPublic;

public class FileRoot extends FileBasic {
    protected FileRoot(String path) {
        super(path);
    }

    @Override
    public boolean exists() {
        return isDirectory() || isFile();
    }

    @Override
    public boolean canRead() {
        String result = KeepShellPublic.doCmdSync("if test -r \"" + getPath() + "\"; then\n" +
                "    echo \"1\"\n" +
                "else\n" +
                "    echo \"0\"\n" +
                "fi");
        return result.equals("1");
    }

    @Override
    public boolean canWrite() {
        String result = KeepShellPublic.doCmdSync("if test -w \"" + getPath() + "\"; then\n" +
                "    echo \"1\"\n" +
                "else\n" +
                "    echo \"0\"\n" +
                "fi");
        return result.equals("1");
    }

    @Override
    public boolean isDirectory() {
        String result = KeepShellPublic.doCmdSync("if test -d \"" + getPath() + "\"; then\n" +
                "    echo \"1\"\n" +
                "else\n" +
                "    echo \"0\"\n" +
                "fi");
        return result.equals("1");
    }

    @Override
    public boolean isFile() {
        String result = KeepShellPublic.doCmdSync("if test -f \"" + getPath() + "\"; then\n" +
                "    echo \"1\"\n" +
                "else\n" +
                "    echo \"0\"\n" +
                "fi");
        return result.equals("1");
    }

    @Override
    public long lastModified() {
        try {
            String result = KeepShellPublic.doCmdSync("lastModified=$(stat -c \"%y\" \"" + getPath() + "\" | cut -d'.' -f1)\n" +
                    "lastModifiedMs=$(date -d \"$lastModified\" +%s%3N)\n" +
                    "echo $lastModifiedMs");
            return Long.parseLong(result.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public long length() {
        try {
            String result = KeepShellPublic.doCmdSync("fileSize=$(stat -c \"%s\" \"" + getPath() + "\")\n" +
                    "echo \"$fileSize\"");
            return Long.parseLong(result.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public boolean createNewFile() {
        String result = KeepShellPublic.doCmdSync("touch \"" + getPath() + "\" && echo \"1\" || echo \"0\"");
        return result.equals("1");
    }

    @Override
    public boolean delete() {
        String result = KeepShellPublic.doCmdSync("rm -rf \"" + getPath() + "\" && echo \"1\" || echo \"0\"");
        return result.equals("1");
    }

    @Override
    public String[] list() {
        String path = getPath();
        String reallyPath = getReallyPath(path);
        String result = KeepShellPublic.doCmdSync("ls -a -l \"" + reallyPath + "\" | grep -v \"^\\./\\|^\\.\\./\"");
        String[] split = result.split("\n");
        List<String> list = new ArrayList<>();
        for (String s : split) {
            try {
                String[] columns = s.split("\\s+");
                String name = columns[7];
                if (!".".equals(name) && !"..".equals(name))
                    list.add(path + "/" + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 有些目录是映射地址，如/sdcard，此方法可以获取真实路径
     *
     * @param path 映射目录
     * @return 真实路径
     */
    private String getReallyPath(String path) {
        try {
            String result = KeepShellPublic.doCmdSync("ls -a -l \"" + path + "\"");
            String line = result.split("\n")[0];
            String[] split = line.split("\\s+");
            String mp = split[9];
            if (!TextUtils.isEmpty(mp)) {
                path = getReallyPath(mp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    public boolean mkdirs() {
        String result = KeepShellPublic.doCmdSync("mkdir -p \"" + getPath() + "\" && echo \"1\" || echo \"0\"");
        return result.equals("1");
    }

    @Override
    public boolean renameTo(String dest) {
        String path = getPath();
        String parent = getParent();
        String result = KeepShellPublic.doCmdSync("mv \"" + path + "\" \"" + parent + "/" + dest + "\" && echo \"1\" || echo \"0\"");
        return result.equals("1");
    }
}
