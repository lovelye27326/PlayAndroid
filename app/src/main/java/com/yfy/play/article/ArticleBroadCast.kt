package com.yfy.play.article

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yfy.core.util.LogUtil

/**
 *  文章广播
 *  描述：PlayAndroid
 *
 */
object ArticleBroadCast {

    const val COLLECT_RECEIVER = "com.yfy.play.COLLECT"

    fun sendArticleChangesReceiver(context: Context) {
        val intent = Intent(COLLECT_RECEIVER)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun setArticleChangesReceiver(c: Activity, block: () -> Unit): BroadcastReceiver {
        val filter = IntentFilter()
        filter.addAction(COLLECT_RECEIVER)
        val r = ArticleBroadcastReceiver(block)
        LocalBroadcastManager.getInstance(c).registerReceiver(r, filter)
        return r
    }

    fun clearArticleChangesReceiver(c: Activity, r: BroadcastReceiver?) {
        r?.apply {
            LocalBroadcastManager.getInstance(c).unregisterReceiver(this)
        }
    }

}

private class ArticleBroadcastReceiver(val block: () -> Unit) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.i("ArticleBroadCastReceiver", "onReceive: ${intent.action}")
        if (intent.action == ArticleBroadCast.COLLECT_RECEIVER) {
            block.invoke()
        }
    }
}