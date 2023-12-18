package com.yfy.play.profile.share

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yfy.model.room.entity.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *
 * 分享
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class ShareViewModel @Inject constructor(private val shareRepository: ShareRepository) :
    ViewModel() {

    val articleList = ArrayList<Article>()

    private val pageLiveData = MutableLiveData<Int>()

    private val pageAndCidLiveData = MutableLiveData<ShareArticle>()

    val articleLiveData = Transformations.switchMap(pageLiveData) { page ->
        shareRepository.getMyShareList(page)
    }

    val articleAndCidLiveData = Transformations.switchMap(pageAndCidLiveData) { page ->
        shareRepository.getShareList(page.cid, page.page)
    }

    fun getArticleList(page: Int) {
        pageLiveData.value = page
    }

    fun getArticleList(cid: Int, page: Int) {
        pageAndCidLiveData.value = ShareArticle(cid, page)
    }


}

data class ShareArticle(var cid: Int, var page: Int)