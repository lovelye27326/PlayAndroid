package com.yfy.play.base.util

import android.app.Activity
import com.yfy.play.main.login.LoginActivity

/**
 * 路由
 * 日期： 2023年12月19日 16:58
 * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *      _              _           _     _   ____  _             _ _
 *     / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *    / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *   / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  -- yfy
 *
 * You never know what you can do until you try !
 * ----------------------------------------------------------------
 */

object ActivityRouter {
    /**
     * 进入登录页
     *
     * @param mActivity from
     */
    fun showLoginActivity(mActivity: Activity) {
        mActivity.startActivity<LoginActivity>()
    }

}
 