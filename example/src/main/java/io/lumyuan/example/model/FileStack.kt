package io.lumyuan.example.model

import java.io.Serializable

/**
 * 文件目录栈
 */
data class FileStack(
    val path: String,
    var scrollPosition: Int
): Serializable
