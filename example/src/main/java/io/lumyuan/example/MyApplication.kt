package io.lumyuan.example

import android.app.Application
import io.lumyuan.filescope.FileApplication

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FileApplication.init(this)
    }

}