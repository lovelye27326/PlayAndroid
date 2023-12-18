package com.yfy.play.article.collect

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseViewModel
import com.yfy.model.model.Collect
import com.yfy.model.model.CollectX
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 收藏列表
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class CollectListViewModel @Inject constructor(private val collectRepository: CollectRepository) :
    BaseViewModel<Collect, CollectX, Int>() {

    override fun getData(page: Int): LiveData<Result<Collect>> {
        return collectRepository.getCollectList(page - 1)
    }

}