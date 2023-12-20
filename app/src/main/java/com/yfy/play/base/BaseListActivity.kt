package com.yfy.play.base

import android.content.res.Configuration
import android.view.View
import com.yfy.play.base.util.isInitialed
import com.yfy.play.base.util.releasableNotNull
import com.yfy.play.base.util.release
import com.yfy.play.databinding.ActivityBaseListBinding
import com.yfy.play.home.ArticleCollectBaseActivity

/**
 *  基类列表页
 *  描述：PlayAndroid
 *
 */
abstract class BaseListActivity : ArticleCollectBaseActivity() {
    protected var binding by releasableNotNull<ActivityBaseListBinding>()
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

    override fun onDestroy() {
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        super.onDestroy()
    }
}
