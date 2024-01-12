package com.yfy.play.home

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.View
import com.yfy.core.view.base.BaseFragment
import com.yfy.play.article.ArticleBroadCast


/**
 * 描述：文章收藏 BaseFragment，注册文章收藏状态改变的广播
 *
 */
abstract class ArticleCollectBaseFragment : BaseFragment() {

    private var articleReceiver: BroadcastReceiver? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleReceiver =
            ArticleBroadCast.setArticleChangesReceiver(requireActivity()) { refreshData() }
    }

    override fun initData() {
        refreshData()
    }

    abstract fun refreshData()

    override fun onDestroy() {
        super.onDestroy()
        ArticleBroadCast.clearArticleChangesReceiver(requireActivity(), articleReceiver)
        articleReceiver = null
    }

}