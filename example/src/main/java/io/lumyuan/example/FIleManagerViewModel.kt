package io.lumyuan.example

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import io.lumyuan.example.model.FileStack
import io.lumyuan.filescope.core.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class FIleManagerViewModel : ViewModel() {

    private val _listFiles = MutableStateFlow<Array<FileItemState>>(arrayOf())
    val listFiles: StateFlow<Array<FileItemState>> = _listFiles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * 压栈
     */
    fun push(pathStack: SnapshotStateList<FileStack>, element: FileStack) {
        pathStack.add(element)
    }

    /**
     * 出栈
     */
    fun pop(pathStack: SnapshotStateList<FileStack>): FileStack? {
        if (pathStack.isNotEmpty()) {
            return pathStack.removeAt(pathStack.size - 1)
        }
        return null
    }

    suspend fun loadList(pathStack: SnapshotStateList<FileStack>, path: String) =
        withContext(Dispatchers.IO) {
            _isLoading.value = true
            push(pathStack, FileStack(path, 0))
            try {
                val itemStates = File(path).listFiles().sortedWith(
                    comparator = compareBy(
                        {
                            it.name
                        },
                        {
                            it.isDirectory
                        }
                    )
                ).map {
                    val directory = it.isDirectory
                    FileItemState(
                        path = it.path,
                        name = it.name,
                        isDirectory = directory,
                        size = if (directory) {
                            try {
                                it.list().size.toLong()
                            }catch (e: Exception) {
                                e.printStackTrace()
                                0L
                            }
                        } else {
                            it.length()
                        },
                        lastModified = it.lastModified()
                    )
                }.toTypedArray()
                if (pathStack.last().path == path) {
                    _listFiles.value = itemStates
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
}