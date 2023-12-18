package com.yfy.play.profile.share

import com.yfy.network.base.PlayAndroidNetwork
import com.yfy.play.base.liveDataModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 *  分享
 *  描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class ShareRepository @Inject constructor() {

    fun getMyShareList(page: Int) = liveDataModel { PlayAndroidNetwork.getMyShareList(page) }

    fun getShareList(cid: Int, page: Int) = liveDataModel { PlayAndroidNetwork.getShareList(cid, page) }

    fun deleteMyArticle(cid: Int) = liveDataModel { PlayAndroidNetwork.deleteMyArticle(cid) }

    fun shareArticle(title: String, link: String) =
        liveDataModel { PlayAndroidNetwork.shareArticle(title, link) }

}