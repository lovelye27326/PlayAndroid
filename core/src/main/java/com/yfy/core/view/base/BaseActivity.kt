package com.yfy.core.view.base

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.yfy.core.R
import com.yfy.core.util.*
import com.yfy.core.view.base.lce.DefaultLceImpl
import com.yfy.core.view.base.lce.ILce


/**
 * 应用程序中所有Activity的基类。
 *
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), ILce, BaseActivityInit {

    /**
     * Activity中显示加载等待的控件。
     */
    private var loading: View? = null

    /**
     * Activity中由于服务器异常导致加载失败显示的布局。
     */
    private var loadErrorView: View? = null

    /**
     * Activity中由于网络异常导致加载失败显示的布局。
     */
    private var badNetworkView: View? = null

    /**
     * Activity中当界面上没有任何内容时展示的布局。
     */
    private var noContentView: View? = null

    private var defaultLce: ILce? = null

    protected var mTAG = this.javaClass.simpleName.toString()

    //设置内容到屏幕顶部的margin是否等于statusbar， 可重写
    open val isContentParentMarginTopEqualStatusBar = true
    open val isFullScreen = true


    override fun onCreate(savedInstanceState: Bundle?) {
        if (isFullScreen) {
            BarUtil.transparentStatusBar(this)
        }
        initWindow()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())
        initView()
        initData()
    }

    override fun initData() {}

    override fun setContentView(view: View?) {
        super.setContentView(view)
        view?.let {
            if (isContentParentMarginTopEqualStatusBar)
                BarUtil.addMarginTopEqualStatusBarHeight(it)
        }
        setupViews()
    }

    open fun initWindow() {}

    protected open fun setupViews() {
        val view = View.inflate(this, R.layout.layout_lce, null)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(
            0,
            dp2px(if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT || isSearchPage()) 70f else 55f),
            0,
            0
        )
        addContentView(view, params)
        loading = view.findViewById(R.id.loading)
        noContentView = view.findViewById(R.id.noContentView)
        badNetworkView = view.findViewById(R.id.badNetworkView)
        loadErrorView = view.findViewById(R.id.loadErrorView)
        if (loading == null) {
            LogUtil.e(mTAG, "loading is null")
        }
        if (badNetworkView == null) {
            LogUtil.e(mTAG, "badNetworkView is null")
        }
        if (loadErrorView == null) {
            LogUtil.e(mTAG, "loadErrorView is null")
        }
        defaultLce = DefaultLceImpl(
            loading,
            loadErrorView,
            badNetworkView,
            noContentView
        )
        loadFinished()
    }

    protected open fun isSearchPage(): Boolean {
        return false
    }

    /**
     * 设置 LiveData 的状态，根据不同状态显示不同页面
     *
     * @param dataLiveData LiveData
     * @param onDataStatus 数据回调进行使用
     */
    fun <T> setDataStatus(dataLiveData: LiveData<Result<T>>, onDataStatus: (T) -> Unit) {
        dataLiveData.observe(this) {
            if (it.isSuccess) {
                val dataList = it.getOrNull()
                if (dataList != null) {
                    loadFinished()
                    onDataStatus(dataList)
                } else {
                    showLoadErrorView()
                }
            } else {
                showShortToast(getString(R.string.bad_network_view_tip))
                showBadNetworkView { initData() }
            }
        }
    }

    @CallSuper
    override fun startLoading() {
        defaultLce?.startLoading()
    }

    @CallSuper
    override fun loadFinished() {
        defaultLce?.loadFinished()
    }

    override fun showLoadErrorView(tip: String) {
        defaultLce?.showLoadErrorView(tip)
    }

    override fun showBadNetworkView(listener: View.OnClickListener) {
        defaultLce?.showBadNetworkView(listener)
    }

    override fun showNoContentView(tip: String) {
        defaultLce?.showNoContentView(tip)
    }


    override fun onStart() {
        super.onStart()
        LogUtil.i("BaseActivity:onStart: $mTAG")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("BaseActivity:onResume: $mTAG")
    }

    override fun onPause() {
        super.onPause()
        LogUtil.i("BaseActivity:onPause: $mTAG")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.i("BaseActivity:onStop: $mTAG")
    }



    override fun onBackPressed() {
        ActivityUtil.finishToActivity(this, true, true) //结束包括当前登录页在内的其他活动页
//        finish()
    }



    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("BaseActivity:onDestroy: $mTAG")
        // 销毁时toast cancel
        cancelToast()
//        ActivityCollector.remove(weakRefActivity)
    }
}
