package com.yfy.play.profile.history

import android.app.Application
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.entity.HISTORY
import com.yfy.play.base.liveDataFire
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 *  历史
 *  描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class BrowseHistoryRepository @Inject constructor(val application: Application) {

    private val browseHistoryDao = PlayDatabase.getDatabase(application).browseHistoryDao()

    /**
     * 获取历史记录列表
     */
    fun getBrowseHistory(page: Int) = liveDataFire {
        val projectClassifyLists = browseHistoryDao.getHistoryArticleList((page - 1) * 20, HISTORY)
        if (projectClassifyLists.isNotEmpty()) {
            Result.success(projectClassifyLists)
        } else {
            Result.failure(RuntimeException("response status is "))
        }

    }

}