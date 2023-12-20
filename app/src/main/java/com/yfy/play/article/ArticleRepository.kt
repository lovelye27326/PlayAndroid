package com.yfy.play.article

import android.app.Application
import com.yfy.core.Play
import com.yfy.core.util.showShortToast
import com.yfy.play.article.collect.CollectRepositoryPoint
import com.yfy.play.R
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 *  文章仓库
 *  描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class ArticleRepository @Inject constructor(val application: Application) :
    CoroutineScope by MainScope() {

    suspend fun setCollect(
        isCollection: Int,
        pageId: Int,
        originId: Int,
        collectListener: (Boolean) -> Unit
    ) {
        coroutineScope {
            launch {
                Play.isLogin().collectLatest {
                    if (!it) {
                        application.showShortToast(R.string.not_currently_logged_in)
                        return@collectLatest
                    }
                }
            }
        }

        if (isCollection == -1 || pageId == -1) {
            application.showShortToast(R.string.page_is_not_collection)
            return
        }
        val collectRepository = EntryPointAccessors.fromApplication(
            application,
            CollectRepositoryPoint::class.java
        ).collectRepository()
        withContext(Dispatchers.IO) {
            if (isCollection == 1) {
                val cancelCollects =
                    collectRepository.cancelCollects(if (originId != -1) originId else pageId)
                if (cancelCollects.errorCode == 0) {
                    application.showShortToast(R.string.collection_cancelled_successfully)
                    ArticleBroadCast.sendArticleChangesReceiver(application)
                    collectListener.invoke(false)
                } else {
                    application.showShortToast(R.string.failed_to_cancel_collection)
                }
            } else {
                val toCollects = collectRepository.toCollects(pageId)
                if (toCollects.errorCode == 0) {
                    application.showShortToast(R.string.collection_successful)
                    ArticleBroadCast.sendArticleChangesReceiver(application)
                    collectListener.invoke(true)
                } else {
                    application.showShortToast(R.string.collection_failed)
                }

            }
        }

    }

}