package com.yfy.play.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yfy.core.util.LogUtil
import com.yfy.model.model.BaseModel
import com.yfy.model.pojo.QueryHomeArticle
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.dao.BannerBeanDao
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.BannerBean
import com.yfy.model.room.entity.HOME
import com.yfy.model.room.entity.HOME_TOP
import com.yfy.network.GlideApp
import com.yfy.play.base.HomeBannerUseCase
import com.yfy.play.base.HomeCommonArticleListUseCase
import com.yfy.play.base.HomeTopArticleListUseCase
import com.yfy.play.base.netRequest
import com.yfy.play.base.util.PreferencesStorage
import com.yfy.play.base.util.TimeUtils
import com.yfy.play.main.login.bean.BannerState
import com.yfy.play.main.login.bean.HomeCommonArticleState
import com.yfy.play.main.login.bean.HomeTopArticleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
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
    private val homeTopArticleListUseCase: HomeTopArticleListUseCase,
    private val homeCommonArticleListUseCase: HomeCommonArticleListUseCase,
    private val homeRepository: HomeRepository
) : ViewModel() {

    companion object {
        private const val KEY_BANNER_INFO = "key_banner_info"
        private const val KEY_TOP_INFO = "key_top_article_info"
        private const val KEY_COMMON_INFO = "key_common_article_info"
    }

    private val _bannerState = savedStateHandle.getLiveData<BannerState>(
        KEY_BANNER_INFO
    )
    val bannerState: LiveData<BannerState>
        get() = _bannerState


    private val _homeTopArticleState = savedStateHandle.getLiveData<HomeTopArticleState>(
        KEY_TOP_INFO
    )
    val homeTopArticleState: LiveData<HomeTopArticleState>
        get() = _homeTopArticleState


    private val _homeCommonArticleState = savedStateHandle.getLiveData<HomeCommonArticleState>(
        KEY_COMMON_INFO
    )
    val homeCommonArticleState: LiveData<HomeCommonArticleState>
        get() = _homeCommonArticleState

    val bannerList = ArrayList<BannerBean>() //vm里缓存HomePageFrg的轮播图数据

    val bannerList2 = ArrayList<BannerBean>()

    val articleList = ArrayList<Article>()

    private val pageLiveData = MutableLiveData<QueryHomeArticle>()

    val articleLiveData = Transformations.switchMap(pageLiveData) { query ->
        homeRepository.getArticleList(query) //通过pageLiveData传入封装好的QueryHomeArticle(page, isRefresh)的query对象进行筛选条件api获取
    }


    fun getArticleList(page: Int, isRefresh: Boolean) {
        pageLiveData.value = QueryHomeArticle(page, isRefresh) //筛选条件封装
    }

    //region 轮播图数据
    /**
     * 封装action，形成类似DSL风格
     */
    fun getHomeBannerInfo() {
        viewModelScope.netRequest {
            start {
                _bannerState.value = BannerState.Loading //MVI 传递对象方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                //可先从本地dataStore取数据判断是否过期，过期需要新数据时再发起网络请求

                getHomeBannerData()
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "HomePageViewModel",
                    "success thread name: " + Thread.currentThread().name
                )
                it?.let { data ->
                    _bannerState.value = BannerState.Success(data)
                } ?: kotlin.run {
                    _bannerState.value = BannerState.Error("null")
                }
            }
            error {
                LogUtil.e("HomePageViewModel", "fail thread name: " + Thread.currentThread().name)
                _bannerState.value = BannerState.Error(it)
            }
            finish {
                _bannerState.value = BannerState.Finished  //MVI 传递对象方式
            }
        }
    }
    //endregion

    //region 轮播图本地或网络获取数据
    private suspend fun getHomeBannerData(): BaseModel<List<BannerBean>> {
        var downImageTime = 0L
        val isCondition: suspend (Long) -> Boolean = {
            downImageTime = it
            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
        }
        preferencesStorage.getLongData(DOWN_IMAGE_TIME, 0L).first(isCondition)
        val formatTime = TimeUtils.formatTimestampWithZone8(downImageTime, "")
        LogUtil.i(
            "HomePageViewModel",
            "downImageFormatTime = $formatTime, downImageTime = $downImageTime"
        )
        val bannerBeanDao = PlayDatabase.getDatabase(application).bannerBeanDao()
        val bannerBeanList = bannerBeanDao.getBannerBeanList()
        return if (bannerBeanList.isNotEmpty() && downImageTime > 0) {
            if (System.currentTimeMillis() - downImageTime < ONE_DAY) {
                LogUtil.i("HomePageViewModel", "downFromDataStore")
                BaseModel(bannerBeanList, 0, "") //还在有效期ONE_DAY内就直接返回
            } else { //超过一天有效期，
                LogUtil.i("HomePageViewModel", "out of time, downFromNet")
                homeBannerUseCase.getHomeBannerInfo()
            }
        } else {
            LogUtil.i("HomePageViewModel", "downFromNet")
            homeBannerUseCase.getHomeBannerInfo()
        }
    }
    //endregion


    //region 轮播图缓存本地和插入数据库
    @SuppressLint("CheckResult")
    suspend fun insertBannerList(
        bannerBeanDao: BannerBeanDao,
        bannerList: List<BannerBean>
    ) {
        val uiScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        bannerList.forEach {
            val mRequestManager: RequestManager = GlideApp.with(application)
            val mRequestBuilder: RequestBuilder<File> = mRequestManager.downloadOnly()
            mRequestBuilder.load(it.imagePath)
            mRequestBuilder.listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean
                ): Boolean {
                    LogUtil.e("HomePageViewModel", "insertBannerList onLoadFailed: ${e?.message}")
                    return false
                }

                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    try {
                        it.filePath = resource?.absolutePath ?: "" //预加载缓存的路径赋值给filePath字段
                        uiScope.launch {
                            if (it.filePath.isNotEmpty()) {
                                bannerBeanDao.insert(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtil.e(
                            "HomePageViewModel",
                            "insertBannerList onResourceReady: ${e.message}"
                        )
                    }
                    return false
                }
            })
            mRequestBuilder.preload()
        }
    }
    //endregion


    //region 头条文章数据
    /**
     * 封装action，形成类似DSL风格
     */
    fun getHomeTopArticleListInfo(query: QueryHomeArticle) {
        viewModelScope.netRequest {
            start {
                _homeTopArticleState.value = HomeTopArticleState.Loading //MVI 传递对象方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                //可先从本地dataStore取数据判断是否过期，过期需要新数据时再发起网络请求
                getHomeTopArticleData(query)
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "HomePageViewModel",
                    "success thread name: " + Thread.currentThread().name
                )
                it?.let { data ->
                    _homeTopArticleState.value = HomeTopArticleState.Success(data)
                } ?: kotlin.run {
                    _homeTopArticleState.value = HomeTopArticleState.Error("null")
                }
            }
            error {
                LogUtil.e("HomePageViewModel", "fail thread name: " + Thread.currentThread().name)
                _homeTopArticleState.value = HomeTopArticleState.Error(it)
            }
            finish {
                _homeTopArticleState.value = HomeTopArticleState.Finished  //MVI 传递对象方式
            }
        }
    }
    //endregion


    //region 头条文章本地或网络获取数据
    private suspend fun getHomeTopArticleData(query: QueryHomeArticle): BaseModel<List<Article>> {
        //先获取热门文章
        var downTopArticleTime = 0L
        val condition: suspend (Long) -> Boolean = {
            downTopArticleTime = it
            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
        }
        preferencesStorage.getLongData(DOWN_TOP_ARTICLE_TIME, 0L)
            .first(condition)
        val formatTime = TimeUtils.formatTimestamp(downTopArticleTime, "")
        LogUtil.i("HomeRepository", "downTopArticleTime = $formatTime")
        val articleListDao = PlayDatabase.getDatabase(application).browseHistoryDao()
        val articleListTop = articleListDao.getTopArticleList(HOME_TOP) //在数据库里筛选热门文章
        return if (articleListTop.isNotEmpty() && downTopArticleTime > 0) {
            if (System.currentTimeMillis() - downTopArticleTime < FOUR_HOUR && !query.isNetRefresh) { //小于缓存保存的时间4小时，且非网络刷新状态时取缓存
                LogUtil.i("HomePageViewModel", "in time, downFromDataBase")
                BaseModel(articleListTop, 0, "") //还在有效期内就直接返回
            } else { //超过一天有效期，
                LogUtil.i("HomePageViewModel", "out of time, downFromNet")
                homeTopArticleListUseCase.getHomeTopArticleListInfo()
            }
        } else {
            LogUtil.i("HomePageViewModel", "downFromNet")
            homeTopArticleListUseCase.getHomeTopArticleListInfo()
        }
    }
    //endregion


    //region 一般文章数据
    /**
     * 封装action，形成类似DSL风格
     */
    fun getHomeCommonArticleListInfo(query: QueryHomeArticle) {
        viewModelScope.netRequest {
            start {
                _homeCommonArticleState.value = HomeCommonArticleState.Loading //MVI 传递对象方式
            }
            request {
//                LogUtil.i(
//                    "LoginViewModelHilt",
//                    "request start thread name: " + Thread.currentThread().name
//                ) //主线程里启动
                //可先从本地dataStore取数据判断是否过期，过期需要新数据时再发起网络请求
                getHomeCommonArticleData(query)
            } //retrofit内部使用非主线程
            success {
                LogUtil.i(
                    "HomePageViewModel",
                    "success thread name: " + Thread.currentThread().name
                )
                it?.let { data ->
                    _homeCommonArticleState.value = HomeCommonArticleState.Success(data)
                } ?: kotlin.run {
                    _homeCommonArticleState.value = HomeCommonArticleState.Error("null")
                }
            }
            error {
                LogUtil.e("HomePageViewModel", "fail thread name: " + Thread.currentThread().name)
                _homeCommonArticleState.value = HomeCommonArticleState.Error(it)
            }
            finish {
                _homeCommonArticleState.value = HomeCommonArticleState.Finished  //MVI 传递对象方式
            }
        }
    }
    //endregion


    //region 一般文章本地或网络获取数据
    private suspend fun getHomeCommonArticleData(query: QueryHomeArticle): BaseModel<List<Article>> {
        //先获取热门文章
        var downCommonArticleTime = 0L
        val condition: suspend (Long) -> Boolean = {
            downCommonArticleTime = it
            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
        }
        preferencesStorage.getLongData(DOWN_ARTICLE_TIME, 0L)
            .first(condition)
        val formatTime = TimeUtils.formatTimestamp(downCommonArticleTime, "")
        LogUtil.i("HomeRepository", "downCommonArticleTime = $formatTime")
        val articleListDao = PlayDatabase.getDatabase(application).browseHistoryDao()
        val articleListHome = articleListDao.getArticleList(HOME) //在数据库里筛选热门文章
        val page = query.page - 1
        return if (articleListHome.isNotEmpty() && downCommonArticleTime > 0) {
            if (System.currentTimeMillis() - downCommonArticleTime < FOUR_HOUR && !query.isNetRefresh && page == 0) { //小于缓存保存的时间4小时，且非网络刷新状态时取缓存
                LogUtil.i("HomePageViewModel", "in time, downFromDataBase, page = $page")
                BaseModel(articleListHome, 0, "") //还在有效期内就直接返回
            } else { //超过一天有效期，
                LogUtil.i("HomePageViewModel", "out of time, downFromNet, page = $page")
                val articleList =
                    homeCommonArticleListUseCase.getHomeCommonArticleListInfo(page)
                BaseModel(articleList.data.datas, 0, "")
            }
        } else {
            LogUtil.i("HomePageViewModel", "downFromNet, page = $page")
            val articleList =
                homeCommonArticleListUseCase.getHomeCommonArticleListInfo(page)
            BaseModel(articleList.data.datas, 0, "")
        }
    }
    //endregion


//    fun getBanner() = homeRepository.getBanner()

}