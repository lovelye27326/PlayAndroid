package com.yfy.play.base

import com.yfy.core.util.LogUtil
import com.yfy.model.model.ArticleList
import com.yfy.model.model.BaseModel
import com.yfy.model.model.Login
import com.yfy.model.model.isSuccess
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.BannerBean
import com.yfy.network.action.RequestAction
import com.yfy.network.exception.HandleException
import com.yfy.network.service.HomePageService
import com.yfy.network.service.LoginService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * desc: 协程http 请求扩展
 */

fun <T> CoroutineScope.netRequest(block: RequestAction<T>.() -> Unit) {
    val action = RequestAction<T>().apply(block)
    this.launch {
        try {
            action.start?.invoke()
            val result = action.request?.invoke()
            if (result.isSuccess()) {
                action.success?.invoke(result!!.data)
            } else {
                if (result != null) {
                    LogUtil.e("ScopeExt", "result: $result")
                    action.error?.invoke(result.errorMsg + "|" + result.errorCode)
                } else {
                    LogUtil.e("ScopeExt", "result: null")
                    action.error?.invoke("null|")
                }
            }
        } catch (ex: Exception) {
            // 可以做一些定制化的返回错误提示
            action.error?.invoke(HandleException.handleResponseError(ex))
        } finally {
            action.finish?.invoke()
        }
    }
}


private const val TAG = "ScopeExt"

@ActivityRetainedScoped
class LoginUseCase @Inject constructor(
    //UseCase的构造函数含单个仓库调用单个函数最佳
    val getLoginInfo: LoginRepository,
)

@ActivityRetainedScoped
class RegisterUseCase @Inject constructor(
    val getRegisterInfo: RegisterRepository
)

@ActivityRetainedScoped
class HomeBannerUseCase @Inject constructor(
    val getHomeBannerInfo: HomeBannerRepository
)

@ActivityRetainedScoped
class HomeTopArticleListUseCase @Inject constructor(
    val getHomeTopArticleListInfo: HomeTopArticleListRepository
)

@ActivityRetainedScoped
class HomeCommonArticleListUseCase @Inject constructor(
    val getHomeCommonArticleListInfo: HomeCommonArticleListRepository
)

@ActivityRetainedScoped
class LoginRepository @Inject constructor(private val service: LoginService) {
    suspend operator fun invoke(username: String, password: String): BaseModel<Login> {
        return service.getLogin(username, password)
    }
}

@ActivityRetainedScoped
class RegisterRepository @Inject constructor(private val service: LoginService) {
    suspend operator fun invoke(
        username: String,
        password: String,
        surePassword: String
    ): BaseModel<Login> {
        return service.getRegister(username, password, surePassword)
    }
}


@ActivityRetainedScoped
class HomeBannerRepository @Inject constructor(private val service: HomePageService) {
    suspend operator fun invoke(): BaseModel<List<BannerBean>> {
        return service.getBanner()
    }
}


@ActivityRetainedScoped
class HomeTopArticleListRepository @Inject constructor(private val service: HomePageService) {
    suspend operator fun invoke(): BaseModel<List<Article>> {
        return service.getTopArticle()
    }
}


@ActivityRetainedScoped
class HomeCommonArticleListRepository @Inject constructor(private val service: HomePageService) {
    suspend operator fun invoke(page: Int): BaseModel<ArticleList> {
        return service.getArticle(page)
    }
}

//region 旧同步http加载状态，当前协程未指定调度线程，恢复挂起的数据仍在当前线程中

fun <T> CoroutineScope.http(
    request: (suspend () -> BaseModel<T>),
    response: (T?) -> Unit,
    error: (String) -> Unit = {},
    showToast: Boolean = true
): Job {
    return this.launch {
        try {
            val result = request()
            if (result.errorCode == 0) {
                response(result.data)
            } else {
                showToast(showToast, result.errorMsg)
                error(result.errorMsg)
            }
        } catch (e: Exception) {
            showToast(showToast, e.message)
            error(e.message ?: "异常")
        }

    }
}

//endregion


//region 旧http协程异步加载方法， 当前协程指定调度线程，恢复挂起的数据仍在当前指定的线程中

fun <T> CoroutineScope.http2(
    request: (suspend () -> BaseModel<T>),
    response: (T?) -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    error: (String) -> Unit = {},
    showToast: Boolean = true
): Job {
    return this.launch(dispatcher) {
        try {
            val result = request()
            if (result.errorCode == 0) {
                response(result.data)
            } else {
                showToast(showToast, result.errorMsg)
                error(result.errorMsg)
            }
        } catch (e: Exception) {
            showToast(showToast, e.message)
            error(e.message ?: "异常")
        }

    }
}

//endregion

//region 提示

private fun showToast(isShow: Boolean, msg: String?) {
    LogUtil.i(TAG, "showToast: isShow:$isShow   msg:$msg")
    if (isShow)
        com.yfy.core.util.showToast(msg)
}

//endregion
