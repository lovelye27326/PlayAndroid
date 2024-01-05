package com.yfy.play.main.login.bean

import com.yfy.core.util.bean.BaseBean
import com.yfy.model.model.Login
import com.yfy.model.room.entity.BannerBean

/**
 * 登录
 * 日期： 2023年12月19日 14:28
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
data class Account(val username: String, val password: String, val isLogin: Boolean): BaseBean()
sealed class LoginState {
    object Logging : LoginState()
    object Finished : LoginState()
    data class LoginSuccess(val login: Login) : LoginState()
    //    object LoginError : LoginState()
    data class LoginError(val errStr: String) : LoginState()
}


sealed class BannerState {
    object Loading : BannerState()
    object Finished : BannerState()
    data class Success(val bannerList: List<BannerBean>) : BannerState()
    data class Error(val errStr: String) : BannerState()
}

