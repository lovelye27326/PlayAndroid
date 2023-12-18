package com.yfy.play.profile.rank

import com.yfy.network.base.PlayAndroidNetwork
import com.yfy.play.base.liveDataModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 排行榜
 * 描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class RankRepository @Inject constructor(){

    /**
     * 获取排行榜列表
     *
     * @param page 页码
     */
    fun getRankList(page: Int) = liveDataModel { PlayAndroidNetwork.getRankList(page) }

    /**
     * 获取个人积分获取列表
     *
     * @param page 页码
     */
    fun getUserRank(page: Int) = liveDataModel { PlayAndroidNetwork.getUserRank(page) }

    /**
     * 获取个人积分信息
     */
    fun getUserInfo() = liveDataModel { PlayAndroidNetwork.getUserInfo() }


}