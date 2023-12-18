package com.yfy.play.home.search

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.room.entity.HotKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 搜索
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseAndroidViewModel<List<HotKey>, HotKey, Boolean>() {

    override fun getData(page: Boolean): LiveData<Result<List<HotKey>>> {
        return searchRepository.getHotKey()
    }

}