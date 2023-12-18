package com.yfy.play.home.search.article

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.model.ArticleList
import com.yfy.model.room.entity.Article
import com.yfy.play.home.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *
 * 文章列表
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseAndroidViewModel<ArticleList, Article, QueryKeyArticle>() {

    override fun getData(page: QueryKeyArticle): LiveData<Result<ArticleList>> {
        return searchRepository.getQueryArticleList(page.page, page.k)
    }

}

data class QueryKeyArticle(var page: Int, var k: String)