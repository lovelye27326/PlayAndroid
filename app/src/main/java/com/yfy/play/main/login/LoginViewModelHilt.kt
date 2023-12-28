package com.yfy.play.main.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yfy.core.Play
import com.yfy.core.util.LogUtil
import com.yfy.core.util.showShortToast
import com.yfy.model.model.Login
import com.yfy.network.action.LoaderState
import com.yfy.play.R
import com.yfy.play.article.ArticleBroadCast
import com.yfy.play.base.LoginUseCase
import com.yfy.play.base.RegisterUseCase
import com.yfy.play.base.http
import com.yfy.play.base.netRequest
import com.yfy.play.main.login.bean.Account
import com.yfy.play.main.login.bean.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author yfy
 * 版本：1.5
 * 创建日期：2020/5/17
 * 描述：PlayAndroid
 *
 * @HiltViewModel 是 Android Jetpack 中用于配合 Dagger Hilt 进行依赖注入的注解，它简化了在使用 ViewModel 时进行依赖注入的过程。
 * 要使用 @HiltViewModel，你需要遵循以下步骤：
 *
添加依赖：
确保你在项目的 build.gradle 文件中添加了 Hilt 和 Hilt Navigation 的相关依赖。
启用 Hilt 构建插件：
在你的应用模块（通常是 app 模块）的 build.gradle 文件中启用 Hilt 插件： apply plugin: 'dagger.hilt.android.plugin'
创建 ViewModel 类：
创建一个继承自 androidx.lifecycle.ViewModel 的类，并用 @HiltViewModel 注解该类。
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MyViewModel @Inject constructor(private val repository: MyRepository) : ViewModel() {
// ...
}
注入依赖：
使用 Dagger Hilt 注解将所需的依赖项注入到 ViewModel 类中。例如，在上面的代码中，我们有一个名为 MyRepository 的依赖项。
配置导航组件：
如果你正在使用 Android Jetpack 导航组件，请确保你已经启用了 HiltNavigation。在你的 MainActivity 或 NavHostFragment 类上使用 @AndroidEntryPoint 注解。
在 Fragment 或 Composable 中获取 ViewModel 实例：
对于 Fragments，你可以使用 by viewModels() 扩展函数来获取 @HiltViewModel 注解的 ViewModel 实例。
class MyFragment : Fragment() {
private val myViewModel: MyViewModel by viewModels()
// ...
}
对于 Jetpack Compose，你可以使用 hiltViewModel() 函数来获取实例。
@Composable
fun MyScreen(navController: NavController) {
val myViewModel = hiltViewModel<MyViewModel>()
// ...
}
构建项目：
现在你应该可以在应用程序中安全地使用被 Hilt 注入的 ViewModel 了。
注意：如果你需要为同一个 ViewModel 类的不同实例提供不同的参数或键值，那么目前 Hilt 并不直接支持这种做法。在这种情况下，你可能需要使用传统的 ViewModelProvider.Factory 方法手动创建 ViewModel 实例。
 *
 *
 */
@HiltViewModel
class LoginViewModelHilt @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    companion object {
        private const val KEY_INFO = "key_info"
//        private const val KEY_STATE = "state"
    }

    private val _state = savedStateHandle.getLiveData<LoginState>(
        KEY_INFO
    )
    val state: LiveData<LoginState>
        get() = _state

    fun toLoginOrRegister(account: Account) {
        if (account.isLogin) {
            login(account)
//            loginHttp(account)
        } else {
            _state.postValue(LoginState.Logging)
            register(account)
        }
    }

    /**
     * 封装action，形成类似DSL风格
     */
    private fun login(account: Account) {
        viewModelScope.netRequest {
            start {
                _state.value = LoginState.Logging //MVI 传递对象方式
//                _stateData.value = LoaderState.STATE_LOADING //或者用liveDate传int方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                loginUseCase.getLoginInfo(account.username, account.password)
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "LoginViewModelHilt",
                    "success thread name: " + Thread.currentThread().name
                )
                success(it, account.isLogin)
            }
            error {
                LogUtil.e("LoginViewModelHilt", "fail thread name: " + Thread.currentThread().name)
                _state.value = LoginState.LoginError(it)
//                if (it.contains("|")) { //liveDate传int方式
//                    _stateData.value = LoaderState.STATE_SOURCE_ERROR
//                } else {
//                    _stateData.value = LoaderState.STATE_NET_ERROR
//                }
            }
            finish {
                _state.value = LoginState.Finished  //MVI 传递对象方式
//                _stateData.value = LoaderState.STATE_SUCCESS //liveDate传int方式
            }
        }
    }


    private fun register(account: Account) {
        viewModelScope.netRequest {
            start {
                _state.value = LoginState.Logging //MVI 传递对象方式
//                _stateData.value = LoaderState.STATE_LOADING //或者用liveDate传int方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                registerUseCase.getRegisterInfo(
                    account.username,
                    account.password,
                    account.password
                )
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "LoginViewModelHilt",
                    "success thread name: " + Thread.currentThread().name
                )
                success(it, account.isLogin)
            }
            error {
                LogUtil.e("LoginViewModelHilt", "fail thread name: " + Thread.currentThread().name)
                _state.value = LoginState.LoginError(it)
//                if (it.contains("|")) { //liveDate传int方式
//                    _stateData.value = LoaderState.STATE_SOURCE_ERROR
//                } else {
//                    _stateData.value = LoaderState.STATE_NET_ERROR
//                }
            }
            finish {
                _state.value = LoginState.Finished  //MVI 传递对象方式
//                _stateData.value = LoaderState.STATE_SUCCESS //liveDate传int方式
            }
        }

    }


    private fun success(it: Login?, isLogin: Boolean) {
        it ?: return
        _state.postValue(LoginState.LoginSuccess(it))
        Play.setLogin(true)
        Play.setUserInfo(it.nickname, it.username)
        ArticleBroadCast.sendArticleChangesReceiver(context = application)
        application.applicationContext.showShortToast(
            if (isLogin) application.getString(R.string.login_success) else application.getString(
                R.string.register_success
            )
        )
    }



    //region 数据加载状态

//    private val _stateData = savedStateHandle.getLiveData<Int>(KEY_STATE)
//    val stateData: LiveData<Int> get() = _stateData
//    init {
//        _stateData.value = LoaderState.STATE_INITIALIZED
//    }

    //endregion


    //region 旧注册方法，方法前后的“region”和“endregion”开头结尾各隔一行，可选中代码区域右键用Ctrl+ALT+T 选择region...endregion Comments项，
    // 可再点击“//“前折叠标志”-“号折叠起来代码

    private fun registerHttp(account: Account) {
        viewModelScope.http(
            request = {
                registerUseCase.getRegisterInfo(
                    account.username,
                    account.password,
                    account.password
                )
            },
            response = { success(it, account.isLogin) },
            error = { _state.postValue(LoginState.LoginError(it)) }
        )
    }

    //endregion

    //region 旧登录方法

    private fun loginHttp(account: Account) {
        _state.postValue(LoginState.Logging)
        viewModelScope.http(
            request = { loginUseCase.getLoginInfo(account.username, account.password) },
            response = { success(it, account.isLogin) },
            error = { _state.postValue(LoginState.LoginError(it)) }
        )
    }

    //endregion


}


