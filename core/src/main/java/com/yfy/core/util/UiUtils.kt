package com.yfy.core.util

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.annotation.IdRes
import com.google.gson.reflect.TypeToken
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.text.DecimalFormat

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

private var sGcWatcher: WeakReference<GcWatcher>? = null

/**
 * 监听gc发生
 */
fun initGcWatcher() {
    if (sGcWatcher == null) {
        LogUtil.i("UiUtils", "GcWatcher is init")
        sGcWatcher = WeakReference(GcWatcher())
    } else {
        LogUtil.e("UiUtils", "GcWatcher is already init,don't need init again")
    }
}

private var sLastGcTime = 0L

private class GcWatcher {
    @kotlin.jvm.Throws(Throwable::class)
    protected fun finalize() {
        val now = SystemClock.uptimeMillis()
        val happenDuration = now - sLastGcTime
        LogUtil.i(
            "UiUtils",
            "happened gc!!！！！！！！!$now - $sLastGcTime = $happenDuration ms interval from last time"
        )
        sLastGcTime = now
        if (ActivityUtil.getActivityList().isNotEmpty())
            sGcWatcher = WeakReference(GcWatcher())
    }
}

fun List<*>?.checkIndex(position: Int?): Boolean {
    val p = position ?: return false
    val l = this ?: return false
    return p >= 0 && p < l.size
}

val List<*>?.length: Int
    get() = this?.size ?: 0


inline fun <T> typeToken(): Type {
    return object : TypeToken<T>() {}.type
}

typealias  Action<T> = ((o: T?) -> Unit)?

fun <T : View> Activity.bindView(@IdRes res: Int): Lazy<T> {
    return lazy { findViewById(res) }
}

fun TextView.setDeleteLine() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

val ActivityResult.isOk
    get() = resultCode == Activity.RESULT_OK

var View.isVisible
    get() = (View.VISIBLE == this.visibility)
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

var View.isInVisible
    get() = (View.INVISIBLE == this.visibility)
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

inline val EXTERNAL_MEDIA_IMAGES_URI_COMPATIBLE_Q: Uri
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }


fun Float?.format2Digits(): String? {
    return if (this == null) null
    else {
        val f = DecimalFormat.getNumberInstance()
        f.maximumFractionDigits = 2
        f.format(this)
    }
}

inline fun <C> C?.ifNullOrBlank(defaultValue: () -> C): C where C : CharSequence =
    if (isNullOrBlank()) defaultValue() else this


fun String?.ifNullOrBlankAction(action: () -> Unit) {
    if (isNullOrBlank()) action()
}

fun String?.notBlankAction(action: (str: String) -> Unit) {
    if (!isNullOrBlank()) action(this)
}