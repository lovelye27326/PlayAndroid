package com.yfy.play.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yfy.model.pojo.QueryHomeArticle
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.BannerBean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 首页
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    val bannerList = ArrayList<BannerBean>() //vm里缓存HomePageFrg的轮播图数据

    val bannerList2 = ArrayList<BannerBean>()

    val articleList = ArrayList<Article>()

    private val pageLiveData = MutableLiveData<QueryHomeArticle>()
    val articleLiveData = Transformations.switchMap(pageLiveData) { query ->
        homeRepository.getArticleList(query) //通过pageLiveData传入封装好的QueryHomeArticle(page, isRefresh)的query对象进行筛选条件api获取
    }

    fun getBanner() = homeRepository.getBanner()

    fun getArticleList(page: Int, isRefresh: Boolean) {
        pageLiveData.value = QueryHomeArticle(page, isRefresh) //筛选条件封装
    }

}