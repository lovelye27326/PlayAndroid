package com.yfy.play.main.login.bean

import android.text.TextUtils
import com.yfy.model.model.Login
import com.yfy.network.util.GsonUtils
import java.io.Serializable

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


open class BaseBean : Serializable { //
    fun toJson(): String {
        return GsonUtils.toJson(this)
    }
}