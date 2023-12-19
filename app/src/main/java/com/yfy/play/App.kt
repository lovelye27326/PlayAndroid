package com.yfy.play

import android.app.Application
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.yfy.core.Play
import com.yfy.play.base.util.*
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


/**
 * Application
 *
 * @author jiang zhu on 2019/10/21
 */
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Play.initialize(applicationContext)
        initData()
    }

    private fun initData() {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            initValidators()
            initBugLy()
        }
    }

    private fun initBugLy() {
        CrashReport.initCrashReport(applicationContext, "8452e4117f", false) //AppId
    }


    /**
     * 输入效验器初始化
     */
    private fun initValidators() {
        Validators.registerValidator(String::class, DefaultStringValidator)
        Validators.registerValidator(Int::class, DefaultIntValidator)
        Validators.registerValidator(Long::class, DefaultLongValidator)
        Validators.registerValidator(Double::class, DefaultDoubleValidator)
        Validators.registerValidator(Boolean::class, DefaultBooleanValidator)
        Validators.registerValidator(Any::class, DefaultAnyValidator)
        Validators.registerValidator(PhoneInfo::class, DefaultPhoneValidator)
        Validators.registerValidator(IdCardNoInfo::class, DefaultIdCardNoValidator)
        Validators.registerValidator(BankCardNoLengthInfo::class, DefaultBankCardNoLengthValidator)
    }


    companion object {
        //static 代码段可以防止内存泄露
        init { //设置全局的Header构建器
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(
                    R.color.refresh,
                    R.color.text_color
                )//全局设置主题颜色  CustomRefreshHeader   ClassicsHeader
                ClassicsHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }
}
