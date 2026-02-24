package com.planup.planup.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun <R> safeRunCatching(block: () -> R): Result<R> = runCatching(block)
    .onFailure { if (it is CancellationException) throw it }

private var toast: Toast? = null
fun Context.showSingleToast(@StringRes message: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        if (toast != null) toast?.setText(message)
        else toast = Toast.makeText(this@showSingleToast, message, Toast.LENGTH_SHORT)

        toast?.show()
    }
}