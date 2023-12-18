package com.yfy.play.profile.rank.user

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseViewModel
import com.yfy.model.model.RankList
import com.yfy.model.model.Ranks
import com.yfy.play.profile.rank.RankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 用户等级
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class UserRankViewModel @Inject constructor(private val rankRepository: RankRepository) :
    BaseViewModel<RankList, Ranks, Int>() {

    override fun getData(page: Int): LiveData<Result<RankList>> {
        return rankRepository.getUserRank(page)
    }

}