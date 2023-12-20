package com.yfy.core.util

import android.content.Context
import android.widget.Toast


fun showToast(msg: String?) {
    if (msg.isNullOrBlank()) return
    ToastUtil.showShort(msg)
}

fun showLongToast(msg: String?) {
    if (msg.isNullOrBlank()) return
    ToastUtil.showLong(msg)
}

private var toast: Toast? = null

fun Context?.showShortToast(
    content: String?
) {
    if (Thread.currentThread().name != "main") return
    if (this == null) return
    if (toast == null) {
        toast = Toast.makeText(
            this,
            content,
            Toast.LENGTH_SHORT
        )
    } else {
        toast?.setText(content)
    }
    toast?.show()
}

fun Context?.showShortToast(resId: Int) {
    if (this == null) return
    if (toast == null) {
        toast = Toast.makeText(
            this,
            resId,
            Toast.LENGTH_SHORT
        )
    } else {
        toast?.setText(resId)
    }
    toast?.show()
}

fun cancelToast() {
    // 销毁时toast cancel
    ToastUtil.cancel()

//    if (Thread.currentThread().name != "main") return
//    if (toast != null) {
//        toast?.cancel()
//        toast = null
//    }
}
