package com.yfy.play.profile.history

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.room.entity.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 历史
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class BrowseHistoryViewModel @Inject constructor(
    private val browseHistoryRepository: BrowseHistoryRepository
) : BaseAndroidViewModel<List<Article>, Article, Int>() {

    override fun getData(page: Int): LiveData<Result<List<Article>>> {
        return browseHistoryRepository.getBrowseHistory(page)
    }

}