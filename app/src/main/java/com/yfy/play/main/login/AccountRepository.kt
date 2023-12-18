package com.yfy.play.main.login

import com.yfy.network.base.PlayAndroidNetwork
import com.yfy.play.base.liveDataModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 账户
 * 描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class AccountRepository @Inject constructor() {

    suspend fun getLogin(username: String, password: String) =
        PlayAndroidNetwork.getLogin(username, password)

    suspend fun getRegister(username: String, password: String, repassword: String) =
        PlayAndroidNetwork.getRegister(username, password, repassword)

    fun getLogout() = liveDataModel { PlayAndroidNetwork.getLogout() }

}