package com.yfy.play.official

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.room.entity.ProjectClassify
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class OfficialViewModel @Inject constructor(
    private val officialRepository: OfficialRepository
) : BaseAndroidViewModel<List<ProjectClassify>, Unit, Boolean>() {

    var position = 0

    override fun getData(page: Boolean): LiveData<Result<List<ProjectClassify>>> {
        return officialRepository.getWxArticleTree(page)
    }

    init {
        getDataList(false)
    }

}