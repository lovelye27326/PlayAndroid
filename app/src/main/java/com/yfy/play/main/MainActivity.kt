package com.yfy.play.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import com.yfy.core.util.*
import com.yfy.core.view.base.BaseActivity
import com.yfy.play.R
import com.yfy.play.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var binding by releasableNotNull<ActivityMainBinding>()
    private val viewModel by viewModels<MainViewModel>()
    var isPort = true //是否竖直

    override fun initView() {
        mTAG = "MainAct"
        isPort = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        when (isPort) {
            true -> binding.homeView?.init(supportFragmentManager, viewModel)
            false -> binding.homeLandView?.init(supportFragmentManager, viewModel)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)  // 解决fragment重影
    }

    override fun getLayoutView(): View {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding.root
    }

    // 用来计算返回键的点击间隔时间
    private var exitTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 3000) {
            //弹出提示，可以有多种方式
            showToast(getString(R.string.exit_program))
            exitTime = System.currentTimeMillis()
        } else {
            ActivityUtil.exitApp()
//            super.onBackPressed() //不用默认的方式
            //返回桌面
//            val i = Intent(Intent.ACTION_MAIN)
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            i.addCategory(Intent.CATEGORY_HOME)
//            startActivity(i)

//            exitProcess(0)

        }
    }


    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onDestroy() {
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        super.onDestroy()
    }

}
