package com.yfy.core.util

import android.content.Context
import android.os.SystemClock
import android.view.View
import java.lang.ref.WeakReference

/**
 * dip转换成pixel
 * @param dpValue
 * @return
 */
fun Context?.dp2px(dpValue: Float): Int {
    if (this == null) return dpValue.toInt()
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

inline fun View.setSafeListener(crossinline action: () -> Unit) {
    var lastClick = 0L
    setOnClickListener {
        val gap = System.currentTimeMillis() - lastClick
        if (gap < 800) return@setOnClickListener
        action.invoke()
        lastClick = System.currentTimeMillis()
    }
}

private var sGcWatcher: WeakReference<GcWatcherInternal>? = null

/**
 * 监听gc发生
 */
fun initGcWatcher() {
    if (sGcWatcher == null) {
        LogUtil.i("UiUtils", "GcWatcher is init")
        sGcWatcher = WeakReference(GcWatcherInternal())
    } else {
        LogUtil.e("UiUtils", "GcWatcher is already init,don't need init again")
    }
}

private var sLastGcTime = 0L

private class GcWatcherInternal {
    @kotlin.jvm.Throws(Throwable::class)
    protected fun finalize() {
        val now = SystemClock.uptimeMillis()
        val happenDuration = now - sLastGcTime
        LogUtil.i("UiUtils", "happened gc!!！！！！！！!$now - $sLastGcTime = $happenDuration ms interval from last time")
        sLastGcTime = now
        sGcWatcher = WeakReference(GcWatcherInternal())
    }
}