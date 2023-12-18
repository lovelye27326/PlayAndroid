package com.yfy.play.profile.rank.list

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseViewModel
import com.yfy.model.model.Rank
import com.yfy.model.model.RankData
import com.yfy.play.profile.rank.RankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 排名
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class RankViewModel @Inject constructor(private val rankRepository: RankRepository) :
    BaseViewModel<RankData, Rank, Int>() {

    override fun getData(page: Int): LiveData<Result<RankData>> {
        return rankRepository.getRankList(page)
    }

}