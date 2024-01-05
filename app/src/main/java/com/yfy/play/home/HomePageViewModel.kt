package com.yfy.play.home

import android.app.Application
import androidx.lifecycle.*
import com.yfy.core.util.LogUtil
import com.yfy.model.model.BaseModel
import com.yfy.model.pojo.QueryHomeArticle
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.BannerBean
import com.yfy.play.base.HomeBannerUseCase
import com.yfy.play.base.netRequest
import com.yfy.play.base.util.PreferencesStorage
import com.yfy.play.base.util.TimeUtils
import com.yfy.play.main.login.bean.BannerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 首页
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class HomePageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val preferencesStorage: PreferencesStorage,
    private val homeBannerUseCase: HomeBannerUseCase,
    private val homeRepository: HomeRepository
) : ViewModel() {

    companion object {
        private const val KEY_INFO = "key_info"
    }

    private val _state = savedStateHandle.getLiveData<BannerState>(
        KEY_INFO
    )
    val state: LiveData<BannerState>
        get() = _state


    val bannerList = ArrayList<BannerBean>() //vm里缓存HomePageFrg的轮播图数据

    val bannerList2 = ArrayList<BannerBean>()

    val articleList = ArrayList<Article>()

    private val pageLiveData = MutableLiveData<QueryHomeArticle>()
    val articleLiveData = Transformations.switchMap(pageLiveData) { query ->
        homeRepository.getArticleList(query) //通过pageLiveData传入封装好的QueryHomeArticle(page, isRefresh)的query对象进行筛选条件api获取
    }

    fun getBanner() = homeRepository.getBanner()

    fun getArticleList(page: Int, isRefresh: Boolean) {
        pageLiveData.value = QueryHomeArticle(page, isRefresh) //筛选条件封装
    }

    /**
     * 封装action，形成类似DSL风格
     */
    fun getBannerInfo() {
        viewModelScope.netRequest {
            start {
                _state.value = BannerState.Loading //MVI 传递对象方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                //可先从本地dataStore取数据判断是否过期，过期需要新数据时再发起网络请求

                getHomeBannerInfo()
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "HomePageViewModel",
                    "success thread name: " + Thread.currentThread().name
                )
                it?.let { data ->
                    _state.value = BannerState.Success(data)
                }?: kotlin.run {
                    _state.value = BannerState.Error("null")
                }
            }
            error {
                LogUtil.e("HomePageViewModel", "fail thread name: " + Thread.currentThread().name)
                _state.value = BannerState.Error(it)
            }
            finish {
                _state.value = BannerState.Finished  //MVI 传递对象方式
            }
        }
    }

    private suspend fun getHomeBannerInfo(): BaseModel<List<BannerBean>> {
        var downImageTime = 0L
        val isCondition: suspend (Long) -> Boolean = {
            downImageTime = it
            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
        }
        preferencesStorage.getLongData(DOWN_IMAGE_TIME, 0L).first(isCondition)
        val formatTime = TimeUtils.formatTimestampWithZone8(downImageTime, "")
        LogUtil.i("HomePageViewModel", "downImageTime = $formatTime")
        val bannerBeanDao = PlayDatabase.getDatabase(application).bannerBeanDao()
        val bannerBeanList = bannerBeanDao.getBannerBeanList()
        if (bannerBeanList.isNotEmpty() && downImageTime > 0 && System.currentTimeMillis() - downImageTime < ONE_DAY) {
            return BaseModel(bannerBeanList, 0, "") //还在有效期就直接返回
        }
        LogUtil.i("HomePageViewModel", "downFromNet")
        return homeBannerUseCase.getHomeBannerInfo()
    }

}