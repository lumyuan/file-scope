package io.lumyuan.example.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.lumyuan.filescope.core.shell.KeepShellPublic
import io.lumyuan.filescope.util.FilePermissionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("Recycle")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Test() {
    val activity = LocalContext.current as ComponentActivity

    // 注册文件权限回调器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        FilePermissionUtil.requestAppSpecificDirectoryPermissionResultLauncher(activity, it)
    }

    var hasRoot by rememberSaveable {
        mutableStateOf(false)
    }

    var path by rememberSaveable {
        mutableStateOf(
            ""
        )
    }

    var text by rememberSaveable { mutableStateOf("") }
    val list = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "File Scope Test Screen")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = path,
                onValueChange = { path = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = {
                    Text(text = "测试指令")
                },
//                singleLine = true,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        hasRoot = KeepShellPublic.checkRoot()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "获取Root权限")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(
                onClick = {
                    if (hasRoot) {

                    }else {
                        Toast.makeText(activity, "未检测到Root权限", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "运行指令")
            }
            SelectionContainer {
                Text(text = text, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp))
            }
            SelectionContainer {
                Column {
                    list.forEach {
                        Text(text = it )
                    }
                }
            }
        }
    }
}