package io.lumyuan.filescope.api;

import io.lumyuan.filescope.core.IOFile;

/**
 * 文件目录权限有效域
 */
@FunctionalInterface
public interface FilePermissionScope {

    void scope(IOFile io);

}
