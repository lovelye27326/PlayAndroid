package com.yfy.play.official.list

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.pojo.QueryArticle
import com.yfy.model.room.entity.Article
import com.yfy.play.official.OfficialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 朋友圈列表
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class OfficialListViewModel @Inject constructor(
    private val officialRepository: OfficialRepository
) : BaseAndroidViewModel<List<Article>, Article, QueryArticle>() {

    override fun getData(page: QueryArticle): LiveData<Result<List<Article>>> {
        return officialRepository.getWxArticle(page)
    }

}

