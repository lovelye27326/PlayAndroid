package com.yfy.play.article.collect

import com.yfy.network.base.PlayAndroidNetwork
import com.yfy.play.base.liveDataModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * 收藏操作
 * 描述：PlayAndroid
 *
 */
@Singleton
class CollectRepository @Inject constructor() {

    /**
     * 获取收藏列表
     *
     * @param page 页码
     */
    fun getCollectList(page: Int) = liveDataModel { PlayAndroidNetwork.getCollectList(page) }

    suspend fun cancelCollects(id: Int) = PlayAndroidNetwork.cancelCollect(id)
    suspend fun toCollects(id: Int) = PlayAndroidNetwork.toCollect(id)

}

@EntryPoint //可以用EntryPointAccessors.fromApplication(mContext, CollectRepositoryPoint::class.java).collectRepository()注入
@InstallIn(SingletonComponent::class)
interface CollectRepositoryPoint {
    fun collectRepository(): CollectRepository
}