package com.yfy.play.base.util

/**
 * 点击防内存泄漏
 * 日期： 2023年04月17日 15:12
 * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *      _              _           _     _   ____  _             _ _
 *     / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *    / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *   / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  -- yfy
 *
 * You never know what you can do until you try !
 * ----------------------------------------------------------------
 */

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import com.simple.spiderman.SpiderMan
import com.yfy.core.util.LogUtil
import com.yfy.play.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <E> SendChannel<E>.safeSend(value: E) = try {
    trySend(value)
} catch (e: CancellationException) {
    LogUtil.e("safeSend", "err: ${e.message}")
    e.printStackTrace()
    SpiderMan.show(e)
}

fun View.clickFlow(): Flow<View> {
    return callbackFlow {
        setOnClickListener {
            safeSend(it)
        }
        awaitClose { setOnClickListener(null) }
    }
}

inline fun View.click(lifecycle: LifecycleCoroutineScope, crossinline onClick: (view: View) -> Unit) {
    clickFlow().onEach {
        onClick(this)
    }.launchIn(lifecycle)
}

/**
 * 延迟第一次点击事件
 *
 * Example：
 *
 * view.clickDelayed(lifecycleScope) {
 *     showShortToast("xxx")
 * }
 */
inline fun View.clickDelayed(
    lifecycle: LifecycleCoroutineScope,
    delayMillis: Long = 500,
    crossinline onClick: (view: View) -> Unit
) {
    clickFlow().onEach {
        delay(delayMillis)
        onClick(this)
    }.launchIn(lifecycle)
}


fun getLastMillis(v: View, id: Int): Long {
    return if (v.getTag(id) != null) v.getTag(id) as Long else 0L
}

fun setLastMillis(v: View, id: Int, millis: Long) {
    v.setTag(id, millis)
}

inline fun View.clickTrigger(
    lifecycle: LifecycleCoroutineScope,
    intervalMillis: Long = 1000,
    crossinline onClick: (view: View) -> Unit
) {
    val id = R.id.tag_pos_key
    clickFlow().onEach {
        val currentMillis = System.currentTimeMillis()
        if (currentMillis - getLastMillis(this, id) < intervalMillis) {
            return@onEach
        }
        setLastMillis(this, id, currentMillis)
        onClick(this)
    }.launchIn(lifecycle)
}
