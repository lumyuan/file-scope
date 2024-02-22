package io.lumyuan.example.common

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

inline fun <VB : ViewBinding> AppCompatActivity.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    inflater(layoutInflater).apply {
        setContentView(this.root)
    }
}

inline fun <VB : ViewBinding> Fragment.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    inflater(layoutInflater)
}