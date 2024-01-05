package com.yfy.play.home

import android.annotation.SuppressLint
import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yfy.core.util.LogUtil
import com.yfy.model.pojo.QueryHomeArticle
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.dao.BannerBeanDao
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.BannerBean
import com.yfy.model.room.entity.HOME
import com.yfy.model.room.entity.HOME_TOP
import com.yfy.network.base.PlayAndroidNetwork
import com.yfy.play.base.liveDataFire
import com.yfy.play.base.util.PreferencesStorage
import com.yfy.play.base.util.TimeUtils
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject


/**
 *
 * 首页
 */
@ActivityRetainedScoped
class HomeRepository @Inject constructor(
    val application: Application,
    private val preferencesStorage: PreferencesStorage
) {

    /**
     * 获取banner
     */
    fun getBanner() = liveDataFire {
        coroutineScope {
//            val dataStore = DataStoreUtils
            var downImageTime = 0L
            val isCondition: suspend (Long) -> Boolean = {
                downImageTime = it
                true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
            }
            preferencesStorage.getLongData(DOWN_IMAGE_TIME, 0L).first(isCondition)
            val formatTime = TimeUtils.formatTimestampWithZone8(downImageTime, "")
            LogUtil.i("HomeRepository", "downImageTime = $formatTime")
            val bannerBeanDao = PlayDatabase.getDatabase(application).bannerBeanDao()
            val bannerBeanList = bannerBeanDao.getBannerBeanList()
            if (bannerBeanList.isNotEmpty() && downImageTime > 0 && System.currentTimeMillis() - downImageTime < ONE_DAY) {
                Result.success(bannerBeanList)
            } else {
                val bannerResponseDeferred =
                    async { PlayAndroidNetwork.getBanner() } //异步
                val bannerResponse = bannerResponseDeferred.await()
                if (bannerResponse.errorCode == 0) {
                    val bannerList = bannerResponse.data
                    preferencesStorage.putLongData(DOWN_IMAGE_TIME, System.currentTimeMillis())
                    if (bannerBeanList.isNotEmpty() && bannerBeanList[0].url == bannerList[0].url) { //数据库本地list数据头条非空且和api返回的头条一致
                        Result.success(bannerBeanList)
                    } else {
                        bannerBeanDao.deleteAll()
                        insertBannerList(bannerBeanDao, bannerList)
                        Result.success(bannerList)
                    }
                } else {
                    Result.failure(RuntimeException("response status is ${bannerResponse.errorCode}  msg is ${bannerResponse.errorMsg}"))
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private suspend fun insertBannerList(
        bannerBeanDao: BannerBeanDao,
        bannerList: List<BannerBean>
    ) {
        val uiScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        bannerList.forEach {
            val mRequestManager: RequestManager = Glide.with(application)
            val mRequestBuilder: RequestBuilder<File> = mRequestManager.downloadOnly()
            mRequestBuilder.load(it.imagePath)
            mRequestBuilder.listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean
                ): Boolean {
                    LogUtil.e("HomeRepository", "insertBannerList onLoadFailed: ${e?.message}")
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
                        it.filePath = resource?.absolutePath ?: ""
                        uiScope.launch {
                            if (it.filePath.isNotEmpty()) {
                                bannerBeanDao.insert(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtil.e(
                            "HomeRepository",
                            "insertBannerList onResourceReady: ${e.message}"
                        )
                    }
                    return false
                }
            })
            mRequestBuilder.preload()
        }
    }

    /**
     * 首页获取文章列表
     * @param query 查询条件
     *
     *
     * Kotlin的Flow<T>.first(predicate: suspend (T) -> Boolean): T函数用于从Flow中获取第一个满足给定predicate函数的元素。
     * 这个函数是suspend函数，这意味着它需要在协程中调用。

    以下是一个使用示例：

    kotlin
    import kotlinx.coroutines.flow.*

    fun main() = runBlocking<Unit> {
    // 创建一个Flow
    val numbers = flow {
    emit(1)
    emit(2)
    emit(3)
    emit(4)
    emit(5)
    }

    // 定义一个predicate函数，检查元素是否是偶数
    val isEven: suspend (Int) -> Boolean = { it % 2 == 0 }

    // 使用first函数获取Flow中第一个偶数
    val firstEvenNumber = numbers.first(isEven)

    println("First even number: $firstEvenNumber") // 输出：First even number: 2
    }
    在这个例子中，我们创建了一个包含一系列整数的Flow。然后我们定义了一个predicate函数isEven，它检查一个整数是否是偶数。
    最后，我们使用first函数和这个predicate函数来获取Flow中的第一个偶数，并打印出来。如果Flow中没有元素满足predicate函数，
    那么first函数会抛出NoSuchElementException异常。
     *
     */
    fun getArticleList(query: QueryHomeArticle) = liveDataFire {
        coroutineScope {
            val res = arrayListOf<Article>()
            if (query.page == 1) {
//                val dataStore = DataStoreUtils
                var downArticleTime = 0L
                val isCondition: suspend (Long) -> Boolean = {
                    downArticleTime = it
                    true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
                }
                preferencesStorage.getLongData(DOWN_ARTICLE_TIME, 0L)
                    .first(isCondition)
                val formatTime = TimeUtils.formatTimestampWithZone8(downArticleTime, "")
                LogUtil.i("HomeRepository", "downArticleTime = $formatTime")
                val articleListDao = PlayDatabase.getDatabase(application).browseHistoryDao()
                val articleListHome = articleListDao.getArticleList(HOME)
                val articleListTop = articleListDao.getTopArticleList(HOME_TOP)
                //先获取热门文章
                var downTopArticleTime = 0L
                val condition: suspend (Long) -> Boolean = {
                    downTopArticleTime = it
                    true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
                }
                preferencesStorage.getLongData(DOWN_TOP_ARTICLE_TIME, 0L)
                    .first(condition)
                val formatTime2 = TimeUtils.formatTimestamp(downTopArticleTime, "")
                LogUtil.i("HomeRepository", "downTopArticleTime = $formatTime2")
                if (articleListTop.isNotEmpty() && downTopArticleTime > 0 &&
                    System.currentTimeMillis() - downTopArticleTime < FOUR_HOUR && !query.isNetRefresh //小于缓存保存的时间4小时，且非网络刷新状态时取缓存
                ) {
                    res.addAll(articleListTop)
                } else {
                    val topArticleListDeferred =
                        async { PlayAndroidNetwork.getTopArticleList() } //异步
                    val topArticleList = topArticleListDeferred.await()
                    if (topArticleList.errorCode == 0) {
                        if (articleListTop.isNotEmpty() && articleListTop[0].link == topArticleList.data[0].link && !query.isNetRefresh) {
                            res.addAll(articleListTop)
                        } else {
                            res.addAll(topArticleList.data)
                            topArticleList.data.forEach {
                                it.localType = HOME_TOP
                            }
                            preferencesStorage.putLongData( //设置保存数据缓存的时间
                                DOWN_TOP_ARTICLE_TIME,
                                System.currentTimeMillis()
                            )
                            articleListDao.deleteAll(HOME_TOP)
                            articleListDao.insertList(topArticleList.data)
                        }
                    }
                }
                //再获取一般文章，叠加一起
                if (articleListHome.isNotEmpty() && downArticleTime > 0 && System.currentTimeMillis() - downArticleTime < FOUR_HOUR
                    && !query.isNetRefresh
                ) {
                    res.addAll(articleListHome)
                    Result.success(res)
                } else {
                    val articleListDeferred =
                        async { PlayAndroidNetwork.getArticleList(query.page - 1) } //异步
                    val articleList = articleListDeferred.await()
                    if (articleList.errorCode == 0) {
                        if (articleListHome.isNotEmpty() && articleListHome[0].link == articleList.data.datas[0].link && !query.isNetRefresh) {
                            res.addAll(articleListHome)
                        } else {
                            res.addAll(articleList.data.datas)
                            articleList.data.datas.forEach {
                                it.localType = HOME
                            }
                            preferencesStorage.putLongData(
                                DOWN_ARTICLE_TIME,
                                System.currentTimeMillis()
                            )
                            articleListDao.deleteAll(HOME)
                            articleListDao.insertList(articleList.data.datas)
                        }
                        Result.success(res)
                    } else {
                        Result.failure(
                            RuntimeException(
                                "response status is ${articleList.errorCode}" + "  msg is ${articleList.errorMsg}"
                            )
                        )
                    }
                }
            } else { //加载第二页开始， 只获取一般文章
                val articleListDeferred =
                    async { PlayAndroidNetwork.getArticleList(query.page - 1) }
                val articleList = articleListDeferred.await()
                if (articleList.errorCode == 0) {
                    res.addAll(articleList.data.datas)
                    Result.success(res)
                } else {
                    Result.failure(
                        RuntimeException(
                            "response status is ${articleList.errorCode}" + "  msg is ${articleList.errorMsg}"
                        )
                    )
                }
            }
        }
    }

}

const val ONE_DAY = 1000 * 60 * 60 * 24
const val FOUR_HOUR = 1000 * 60 * 60 * 4
const val DOWN_IMAGE_TIME = "DownImageTime"
const val DOWN_TOP_ARTICLE_TIME = "DownTopArticleTime"
const val DOWN_ARTICLE_TIME = "DownArticleTime"
const val DOWN_PROJECT_ARTICLE_TIME = "DownProjectArticleTime"
const val DOWN_OFFICIAL_ARTICLE_TIME = "DownOfficialArticleTime"