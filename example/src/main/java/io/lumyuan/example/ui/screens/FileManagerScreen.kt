package io.lumyuan.example.ui.screens

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.lumyuan.example.FIleManagerViewModel
import io.lumyuan.example.FileItemState
import io.lumyuan.example.model.FileStack
import io.lumyuan.filescope.FileScope
import io.lumyuan.filescope.data.FileFramework
import io.lumyuan.filescope.util.FilePermissionUtil
import io.lumyuan.filescope.util.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen() {
    val activity = LocalContext.current as ComponentActivity
    val fIleManagerViewModel: FIleManagerViewModel = viewModel()
    val ioScope = rememberCoroutineScope { Dispatchers.IO }

    val pathStack = remember { mutableStateListOf<FileStack>() }
    val listFilesState = fIleManagerViewModel.listFiles.collectAsStateWithLifecycle()
    val isLoadingState = fIleManagerViewModel.isLoading.collectAsStateWithLifecycle()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            FilePermissionUtil.requestAppSpecificDirectoryPermissionResultLauncher(activity, it)
        }

    val staggeredGridState = rememberLazyStaggeredGridState()

    BackHandler(pathStack.size > 1) {
        ioScope.launch {
            fIleManagerViewModel.pop(pathStack)?.also {
                fIleManagerViewModel.loadList(pathStack, java.io.File(it.path).parent ?: "/")
                ioScope.launch(Dispatchers.Main) {
                    staggeredGridState.animateScrollToItem(it.scrollPosition)
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (pathStack.isEmpty()) {
            //初始化底栈
            fIleManagerViewModel.push(
                pathStack,
                FileStack(
                    path = Environment.getExternalStorageDirectory().absolutePath,
                    scrollPosition = 0
                )
            )
        }
        if (listFilesState.value.isEmpty()) {
            //判断权限
            FileScope.filePermissionScope(activity, pathStack.last().path, launcher) {
                ioScope.launch {
                    fIleManagerViewModel.loadList(pathStack, pathStack.last().path)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            activity.finish()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Column {
                        Text(text = "简易文件管理器", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = try {
                                pathStack.last().path
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ""
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                actions = {
                    Column {
                        val menuState = rememberSaveable { mutableStateOf(false) }
                        IconButton(
                            onClick = {
                                menuState.value = true
                            }
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        TopMenu(menuState, fIleManagerViewModel, pathStack, activity, launcher, ioScope)
                    }
                }
            )
        }
    ) {
        val view: @Composable () -> Unit = {
            if (listFilesState.value.isEmpty() && (!pathStack.isEmpty() && pathStack.last().path == Environment.getExternalStorageDirectory().path)) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            FileScope.filePermissionScope(
                                activity,
                                pathStack.last().path,
                                launcher
                            ) {
                                ioScope.launch {
                                    fIleManagerViewModel.loadList(pathStack, pathStack.last().path)
                                }
                            }
                        }
                    ) {
                        Text(text = "点击刷新")
                    }
                }
            } else {
                FileList(
                    paddingValues = it,
                    staggeredGridState,
                    listFilesState = listFilesState,
                    onItemClick = { fileItemState ->
                        if (fileItemState.isDirectory) {
                            ioScope.launch {
                                pathStack.last().scrollPosition =
                                    staggeredGridState.firstVisibleItemIndex
                                fIleManagerViewModel.loadList(pathStack, fileItemState.path)
                            }
                        } else {
                            Toast.makeText(activity, "打开文件功能自己实现吧", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
        }
        AnimatedContent(targetState = view, label = "view change") {
            it()
        }
    }
    LoadingDialog(isLoadingState)
}

@Composable
private fun ColumnScope.TopMenu(
    menuState: MutableState<Boolean>,
    fIleManagerViewModel: FIleManagerViewModel,
    pathStack: SnapshotStateList<FileStack>,
    activity: ComponentActivity,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    ioScope: CoroutineScope
) {
    var fileFrameworkMenuState by rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded = menuState.value,
        onDismissRequest = { menuState.value = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "切换IO框架")
            },
            onClick = {
                fileFrameworkMenuState = true
            }
        )
    }

    DropdownMenu(
        expanded = fileFrameworkMenuState,
        onDismissRequest = { fileFrameworkMenuState = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "原生IO")
            },
            onClick = {
                fileFrameworkMenuState = false
                menuState.value = false
                FileScope.setLocalFileFramework(FileFramework.NATIVE)
                FileScope.filePermissionScope(activity, pathStack.last().path, launcher) {
                    ioScope.launch {
                        fIleManagerViewModel.loadList(pathStack, pathStack.last().path)
                    }
                }
            },
            trailingIcon = {
                if (FileScope.getLocalFileFramework() == FileFramework.NATIVE) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "ROOT")
            },
            onClick = {
                fileFrameworkMenuState = false
                menuState.value = false
                FileScope.setLocalFileFramework(FileFramework.SU)
                FileScope.filePermissionScope(activity, pathStack.last().path, launcher) {
                    ioScope.launch {
                        fIleManagerViewModel.loadList(pathStack, pathStack.last().path)
                    }
                }
            },
            trailingIcon = {
                if (FileScope.getLocalFileFramework() == FileFramework.SU) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Shizuku")
            },
            onClick = {
                fileFrameworkMenuState = false
                menuState.value = false
                Toast.makeText(activity, "该功能待实现", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingDialog(isLoadingState: State<Boolean>) {
    if (isLoadingState.value) {
        AlertDialog(
            onDismissRequest = { }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = "加载中...")
                }
            }
        }
    }
}

@Composable
private fun FileList(
    paddingValues: PaddingValues,
    staggeredGridState: LazyStaggeredGridState,
    listFilesState: State<Array<FileItemState>>,
    onItemClick: (FileItemState) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        state = staggeredGridState,
        columns = StaggeredGridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(
            items = listFilesState.value
        ) {
            FileItem(it, onItemClick)
        }
    }
}

private val dataFormat by lazy {
    SimpleDateFormat("yyyy/MM/dd")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileItem(
    fileItemState: FileItemState,
    onItemClick: (FileItemState) -> Unit,
) {
    val itemDropdownMenuState = rememberSaveable { mutableStateOf(false) }
    val isDirectory = fileItemState.isDirectory
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onItemClick(fileItemState)
                    },
                    onLongClick = {
                        itemDropdownMenuState.value = true
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isDirectory) {
                    Icons.Default.Folder
                } else {
                    Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = fileItemState.name,
                    style = MaterialTheme.typography.bodyMedium,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                if (isDirectory) {
                    Text(
                        text = "${dataFormat.format(Date(fileItemState.lastModified))} | ${fileItemState.size}项",
                        style = MaterialTheme.typography.labelMedium,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Text(
                        text = "${dataFormat.format(Date(fileItemState.lastModified))} | ${
                            FileUtil.readableFileSize(
                                fileItemState.size
                            )
                        }",
                        style = MaterialTheme.typography.labelMedium,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Column {
                if (isDirectory) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                FileItemMenu(itemDropdownMenuState, fileItemState)
            }
        }
    }
}

@Composable
private fun ColumnScope.FileItemMenu(state: MutableState<Boolean>, fileItemState: FileItemState) {
    val context = LocalContext.current
    DropdownMenu(
        expanded = state.value,
        onDismissRequest = { state.value = false },
        modifier = Modifier.align(Alignment.End)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DriveFileRenameOutline,
                    contentDescription = null
                )
            },
            text = {
                Text(text = "重命名")
            },
            onClick = {
                state.value = false
                Toast.makeText(
                    context,
                    "请自行调用io.lumyuan.filescope.core.File.renameTo(String dest)方法",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }


}
