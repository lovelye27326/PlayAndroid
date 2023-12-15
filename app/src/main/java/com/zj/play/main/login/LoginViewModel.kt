package com.zj.play.main.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zj.core.Play
import com.zj.core.util.showToast
import com.zj.model.model.Login
import com.zj.play.R
import com.zj.play.article.ArticleBroadCast
import com.zj.play.base.http
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 版权：Zhujiang 个人版权
 * @author zhujiang
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
class LoginViewModel @Inject constructor(
    application: Application,
    private val accountRepository: AccountRepository
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState>
        get() = _state

    fun toLoginOrRegister(account: Account) {
        _state.postValue(Logging)
        if (account.isLogin) {
            login(account)
        } else {
            register(account)
        }
    }

    private fun login(account: Account) {
        viewModelScope.http(
            request = { accountRepository.getLogin(account.username, account.password) },
            response = { success(it, account.isLogin) },
            error = { _state.postValue(LoginError) }
        )
    }


    private fun register(account: Account) {
        viewModelScope.http(
            request = {
                accountRepository.getRegister(
                    account.username,
                    account.password,
                    account.password
                )
            },
            response = { success(it, account.isLogin) },
            error = { _state.postValue(LoginError) }
        )
    }

    private fun success(it: Login?, isLogin: Boolean) {
        it ?: return
        _state.postValue(LoginSuccess(it))
        Play.setLogin(true)
        Play.setUserInfo(it.nickname, it.username)
        ArticleBroadCast.sendArticleChangesReceiver(context = getApplication())
        getApplication<Application>().showToast(
            if (isLogin) getApplication<Application>().getString(R.string.login_success) else getApplication<Application>().getString(
                R.string.register_success
            )
        )
    }

}

data class Account(val username: String, val password: String, val isLogin: Boolean)
sealed class LoginState
object Logging : LoginState()
data class LoginSuccess(val login: Login) : LoginState()
object LoginError : LoginState()
