package com.yfy.play.base

import android.content.res.Configuration
import android.view.View
import com.yfy.play.databinding.ActivityBaseListBinding
import com.yfy.play.home.ArticleCollectBaseActivity

/**
 *  基类列表页
 *  描述：PlayAndroid
 *
 */
abstract class BaseListActivity : ArticleCollectBaseActivity() {

    protected lateinit var binding: ActivityBaseListBinding
    protected var page = 1

    override fun getLayoutView(): View {
        binding = ActivityBaseListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData() {
        getDataList()
    }

    abstract fun getDataList()

    override fun initView() {
        binding.baseListToTop.setRecyclerViewLayoutManager(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        binding.baseListToTop.onRefreshListener({
            page = 1
            getDataList()
        }, {
            page++
            getDataList()
        })
    }

    abstract fun isStaggeredGrid(): Boolean

}
