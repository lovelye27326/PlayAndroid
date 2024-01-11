package com.yfy.play.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.yfy.core.util.*
import com.yfy.core.util.ScreenUtils.dp2px
import com.yfy.model.pojo.QueryHomeArticle
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.entity.BannerBean
import com.yfy.model.room.entity.HOME
import com.yfy.model.room.entity.HOME_TOP
import com.yfy.play.R
import com.yfy.play.article.ArticleActivity
import com.yfy.play.article.ArticleAdapter
import com.yfy.play.base.util.PreferencesStorage
import com.yfy.play.base.util.TimeUtils
import com.yfy.play.databinding.FragmentHomePageBinding
import com.yfy.play.home.almanac.AlmanacActivity
import com.yfy.play.home.search.SearchActivity
import com.yfy.play.main.MainActivity
import com.yfy.play.main.login.bean.BannerState
import com.yfy.play.main.login.bean.HomeCommonArticleState
import com.yfy.play.main.login.bean.HomeTopArticleState
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.transformer.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomePageFragment : ArticleCollectBaseFragment() {
    private val viewModel by viewModels<HomePageViewModel>()
    private var binding by releasableNotNull<FragmentHomePageBinding>()
    private var isStarted: Boolean = false
    private var bannerAdapter by releasableNotNull<ImageAdapter>()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, attachToRoot)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.apply {
            if (!isStarted && Validators[Int::class].validate(bannerAdapter.realCount)) {
                isStarted = true
                homeBanner.start()
            }
        }
    }

    override fun refreshData() {
//        viewModel.getHomeTopArticleListInfo(QueryHomeArticle(page, true))

//        getArticleList(true) //避免重复请求
    }


    @Inject
    lateinit var preferencesStorage: PreferencesStorage

    private lateinit var articleAdapter: ArticleAdapter
    private var page = 1

    override fun initView() {
        binding.apply {
            homeTitleBar.setRightImage(R.drawable.home_search_button)
            if (isZhLanguage()) {
                homeTitleBar.setTitleOnClickListener {
                    AlmanacActivity.actionStart(requireContext())
                }
            }
            homeTitleBar.setRightImgOnClickListener {
                SearchActivity.actionStart(requireContext())
                activity?.overridePendingTransition(R.anim.search_push_in, R.anim.fake_anim)
            }


            homeBanner.apply {
                // 添加生命周期管理，确保在适当的生命周期内开始和停止轮播
                addBannerLifecycleObserver(viewLifecycleOwner) //注意避免重复监听
                setPageTransformer(ZoomOutPageTransformer()) //setPageTransformer(DepthPageTransformer())

                bannerAdapter = ImageAdapter(this@HomePageFragment)
                adapter = bannerAdapter
                setBannerRound(20f)
                indicator = CircleIndicator(context) //.start()

                isAutoLoop(true)

                setIndicatorWidth(
                    dp2px(
                        requireActivity(),
                        5f
                    ), dp2px(
                        requireActivity(),
                        5f
                    )
                )
                setIndicatorNormalColor(Color.parseColor("#FFFFFF"))
                setIndicatorSelectedColor(Color.parseColor("#000000"))
                setIndicatorSpace(
                    dp2px(
                        requireActivity(),
                        6f
                    )
                )
                setLoopTime(3000)
                // 设置轮播图的点击事件监听器，点了哪张图执行自定义逻辑
                setOnBannerListener { data, _ ->
                    val bannerData = data as BannerBean
                    val url = bannerData.url.ifNullOrBlank { "https:\\www.baidu.com" }
                    val title = bannerData.title.ifNullOrBlank { "百度" }
                    ArticleActivity.actionStart(
                        requireActivity(),
                        title,
                        url
                    )
                }
            }

            homeToTopRecyclerView.setRecyclerViewLayoutManager(true)
            articleAdapter = ArticleAdapter(viewModel.articleList, true, this@HomePageFragment)
            homeToTopRecyclerView.onRefreshListener({
                page = 1
                LogUtil.i("HomePageFrg", "refresh page = $page")
                viewModel.getHomeTopArticleListInfo(
                    QueryHomeArticle(
                        page,
                        true
                    )
                ) //头条文章API只用到QueryHomeArticle对象的isNetRefresh字段
//                getArticleList(true)
            }, {
                page++
                LogUtil.i("HomePageFrg", "loadMore page = $page")
                //只加载一般文章
                viewModel.getHomeCommonArticleListInfo(QueryHomeArticle(page, false))
//                getArticleList(false)
            })
            homeToTopRecyclerView.setAdapter(articleAdapter)
        }


        viewModel.bannerState.observe(this) { info ->
            when (info) {
                BannerState.Loading -> { //Loading是object声明的，不用is判断
                    startLoading() //判断弱网情况加载结束
                }
                is BannerState.Success -> {
                    loadFinished()
                    val size = info.bannerList.size
                    LogUtil.i("HomePageFrg", "List size: $size")
                    val main = activity as MainActivity
                    if (viewModel.bannerList.size > 0)
                        viewModel.bannerList.clear()
                    if (viewModel.bannerList2.size > 0)
                        viewModel.bannerList2.clear()
                    if (main.isPort) {
                        viewModel.bannerList.addAll(info.bannerList)
                    } else {
                        for (index in info.bannerList.indices) { //横屏
                            if (index / 2 == 0) {
                                viewModel.bannerList.add(info.bannerList[index])
                            } else {
                                viewModel.bannerList2.add(info.bannerList[index])
                            }
                        }
                    }
                    val bannerList = viewModel.bannerList
                    binding.homeBanner.setDatas(bannerList)
                    if (!isStarted) {
                        isStarted = true
                        binding.homeBanner.start()
                    }
                    binding.homeBanner.start() //开始轮播

                    viewModel.viewModelScope.launch { //保存本地dataStore
                        var downImageTime = 0L
                        val isCondition: suspend (Long) -> Boolean = {
                            downImageTime = it
                            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
                        }
                        preferencesStorage.getLongData(DOWN_IMAGE_TIME, 0L).first(isCondition)
                        val formatTime = TimeUtils.formatTimestampWithZone8(downImageTime, "")
                        LogUtil.i(
                            "HomePageFrg",
                            "downImageFormatTime = $formatTime, downImageTime = $downImageTime"
                        )
                        val bannerBeanDao =
                            PlayDatabase.getDatabase(ActivityUtil.getTopActivityOrApp())
                                .bannerBeanDao()
                        val bannerBeanList = bannerBeanDao.getBannerBeanList()
                        if (downImageTime == 0L || System.currentTimeMillis() - downImageTime >= ONE_DAY) {
                            if (bannerBeanList.isNotEmpty()) { //数据库本地list数据非空进行判断
                                if (bannerBeanList[0].url != bannerList[0].url) { //数据库本地list数据第一条（index = 0）和api返回的第一条的url字段一致
                                    LogUtil.i("HomePageFrg", "dataBase not null, put banner")
                                    preferencesStorage.putLongData(
                                        DOWN_IMAGE_TIME,
                                        System.currentTimeMillis()
                                    )
                                    bannerBeanDao.deleteAll()
                                    viewModel.insertBannerList(
                                        bannerBeanDao,
                                        bannerList
                                    ) //预加载图片和插入数据库
                                }
                            } else {
                                LogUtil.i("HomePageFrg", "dataBase null, put banner")
                                preferencesStorage.putLongData(
                                    DOWN_IMAGE_TIME,
                                    System.currentTimeMillis()
                                )
                                bannerBeanDao.deleteAll()
                                viewModel.insertBannerList(bannerBeanDao, bannerList) //预加载图片和插入数据库
                            }
                        }
                    }
                }
                is BannerState.Error -> {
//                    showLoadErrorView() //判断弱网情况加载结束
                    LogUtil.i("HomePageFrg", "err: ${info.errStr}")
                    if (info.errStr.contains("|")) {
                        val toastStr = info.errStr.split("|")[0]
                        showToast(toastStr)
                    } else {
                        showToast(info.errStr)
                    }
                }
                BannerState.Finished -> {
                    loadFinished() //判断弱网情况加载结束
                }
                else -> {}
            }
        }


        viewModel.homeTopArticleState.observe(this) { info ->
            when (info) {
                HomeTopArticleState.Loading -> { //Loading是object声明的，不用is判断
                    startLoading() //判断弱网情况加载结束
                }
                is HomeTopArticleState.Success -> {
                    loadFinished()
                    val size = info.articleList.size
                    LogUtil.i("HomePageFrg", "topArticleList size: $size")
                    if (viewModel.articleList.size > 0)
                        viewModel.articleList.clear()
                    val articleList = info.articleList
                    viewModel.articleList.addAll(articleList)
//                    articleAdapter.notifyItemInserted(size) //等再加载完一般文章后再通知变化

                    viewModel.viewModelScope.launch { //保存本地dataStore
                        var downTopArticleTime = 0L
                        val isCondition: suspend (Long) -> Boolean = {
                            downTopArticleTime = it
                            true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
                        }
                        preferencesStorage.getLongData(DOWN_TOP_ARTICLE_TIME, 0L).first(isCondition)
                        val formatTime = TimeUtils.formatTimestampWithZone8(downTopArticleTime, "")
                        LogUtil.i(
                            "HomePageFrg",
                            "downTopArticleFormatTime = $formatTime, downTopArticleTime = $downTopArticleTime"
                        )

                        val articleListDao =
                            PlayDatabase.getDatabase(ActivityUtil.getTopActivityOrApp())
                                .browseHistoryDao()
                        val articleListTop =
                            articleListDao.getTopArticleList(HOME_TOP) //在数据库里筛选热门头条文章
                        if (downTopArticleTime == 0L || System.currentTimeMillis() - downTopArticleTime >= FOUR_HOUR) {
                            if (articleListTop.isNotEmpty()) { //数据库本地list数据非空进行判断
                                if (articleListTop[0].link != articleList[0].link) { //数据库本地list数据第一条（index = 0）和api返回的第一条的url字段一致
                                    LogUtil.i("HomePageFrg", "dataBase not null, put article")
                                    preferencesStorage.putLongData(
                                        DOWN_TOP_ARTICLE_TIME,
                                        System.currentTimeMillis()
                                    )

                                    articleList.forEach {
                                        it.localType = HOME_TOP //设置文章本地类型为头条
                                    }
                                    articleListDao.deleteAll(HOME_TOP)
                                    articleListDao.insertList(articleList)
                                }
                            } else {
                                LogUtil.i("HomePageFrg", "dataBase null, put article")
                                preferencesStorage.putLongData(
                                    DOWN_TOP_ARTICLE_TIME,
                                    System.currentTimeMillis()
                                )
                                articleList.forEach {
                                    it.localType = HOME_TOP
                                }
                                articleListDao.deleteAll(HOME_TOP)
                                articleListDao.insertList(articleList) //插入数据库
                            }
                        }
                    }

                }
                is HomeTopArticleState.Error -> {
//                    showLoadErrorView() //判断弱网情况加载结束
                    LogUtil.i("HomePageFrg", "err: ${info.errStr}")
                    if (info.errStr.contains("|")) {
                        val toastStr = info.errStr.split("|")[0]
                        showToast(toastStr)
                    } else {
                        showToast(info.errStr)
                    }
                }
                HomeTopArticleState.Finished -> {
//                    loadFinished() //判断弱网情况加载结束
                    //再加载一般文章
                    viewModel.getHomeCommonArticleListInfo(QueryHomeArticle(page, false))
                }
                else -> {}
            }
        }


        viewModel.homeCommonArticleState.observe(this) { info ->
            when (info) {
                HomeCommonArticleState.Loading -> { //Loading是object声明的，不用is判断
                    if (page != 1)
                        startLoading() //判断弱网情况加载结束
                }
                is HomeCommonArticleState.Success -> {
                    loadFinished()
                    val size = info.articleList.size
                    LogUtil.i("HomePageFrg", "articleList size: $size")
                    val oldSize = viewModel.articleList.size
                    val articleList = info.articleList
                    viewModel.articleList.addAll(articleList)
                    if (page != 1) {
                        articleAdapter.notifyItemRangeChanged(
                            oldSize,
                            size
                        )
                    } else {
                        articleAdapter.notifyItemInserted(size)
                    }
                    if (page == 1) { //一般文章首页需要判断保存到数据库
                        viewModel.viewModelScope.launch { //保存本地dataStore
                            var downCommonArticleTime = 0L
                            val isCondition: suspend (Long) -> Boolean = {
                                downCommonArticleTime = it
                                true //总返回真， 传入first只取第一个后自动结束流收集， 和launchIn(coroutineScope)传入viewModel作用域自动结束收集功能类似
                            }
                            preferencesStorage.getLongData(DOWN_ARTICLE_TIME, 0L).first(isCondition)
                            val formatTime =
                                TimeUtils.formatTimestampWithZone8(downCommonArticleTime, "")
                            LogUtil.i(
                                "HomePageFrg",
                                "downCommonArticleFormatTime = $formatTime, downCommonArticleTime = $downCommonArticleTime"
                            )

                            val articleListDao =
                                PlayDatabase.getDatabase(ActivityUtil.getTopActivityOrApp())
                                    .browseHistoryDao()
                            val articleListHome =
                                articleListDao.getArticleList(HOME) //在数据库里筛选热门一般文章
                            if (downCommonArticleTime == 0L || System.currentTimeMillis() - downCommonArticleTime >= FOUR_HOUR) {
                                if (articleListHome.isNotEmpty()) { //数据库本地list数据非空进行判断
                                    if (articleListHome[0].link != articleList[0].link) { //数据库本地list数据第一条（index = 0）和api返回的第一条的url字段一致
                                        LogUtil.i("HomePageFrg", "dataBase not null, put article")
                                        preferencesStorage.putLongData(
                                            DOWN_ARTICLE_TIME,
                                            System.currentTimeMillis()
                                        )

                                        articleList.forEach {
                                            it.localType = HOME //设置文章本地类型为一般文章
                                        }
                                        articleListDao.deleteAll(HOME)
                                        articleListDao.insertList(articleList)
                                    }
                                } else {
                                    LogUtil.i("HomePageFrg", "dataBase null, put article")
                                    preferencesStorage.putLongData(
                                        DOWN_ARTICLE_TIME,
                                        System.currentTimeMillis()
                                    )
                                    articleList.forEach {
                                        it.localType = HOME
                                    }
                                    articleListDao.deleteAll(HOME)
                                    articleListDao.insertList(articleList) //插入数据库
                                }
                            }
                        }
                    }
                }
                is HomeCommonArticleState.Error -> {
                    showLoadErrorView() //判断弱网情况加载结束
                    LogUtil.i("HomePageFrg", "err: ${info.errStr}")
                    if (info.errStr.contains("|")) {
                        val toastStr = info.errStr.split("|")[0]
                        showToast(toastStr)
                    } else {
                        showToast(info.errStr)
                    }
                }
                HomeCommonArticleState.Finished -> {
                    loadFinished() //判断弱网情况加载结束
                }
                else -> {}
            }
        }


//        setDataStatus(viewModel.articleLiveData, {
//            if (viewModel.articleList.size > 0) loadFinished() //判断弱网情况下且已加载的articleList非空则显示加载结束
//        }) {
//            if (page == 1 && viewModel.articleList.size > 0) {
//                viewModel.articleList.clear()
//            }
//            viewModel.articleList.addAll(it)
//            articleAdapter.notifyItemInserted(it.size)
//        }
    }

    override fun initData() {
        viewModel.getHomeBannerInfo()
        viewModel.getHomeTopArticleListInfo(QueryHomeArticle(page, false))

//        initBanner()
//        startLoading()
//        getArticleList(false)
    }


    override fun onPause() {
        super.onPause()
        binding.apply {
            if (isStarted) {
                isStarted = false
                binding.homeBanner.stop()
            }
        }
    }

    override fun onDestroy() {
        binding.homeBanner.viewPager2.adapter = null
        if (::bannerAdapter.isInitialed()) {
            bannerAdapter.clean()
            ::bannerAdapter.release()
        }
        super.onDestroy()
        if (::binding.isInitialed()) {
            ::binding.release()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomePageFragment()
    }

    //        private lateinit var bannerAdapter2: ImageAdapter

//    private fun getArticleList(isRefresh: Boolean) {
//        viewModel.getArticleList(page, isRefresh)
//    }


//    @SuppressLint("NotifyDataSetChanged")
//    private fun initBanner() {
//        setDataStatus(viewModel.getBanner(), {
//            if (viewModel.bannerList.size > 0) loadFinished() //判断弱网情况加载结束
//        }) {
//            val size = it.size
//            LogUtil.i("HomePageFrg", "List size: $size")
//            val main = activity as MainActivity
//            if (viewModel.bannerList.size > 0)
//                viewModel.bannerList.clear()
//            if (viewModel.bannerList2.size > 0)
//                viewModel.bannerList2.clear()
//            if (main.isPort) {
//                viewModel.bannerList.addAll(it)
//            } else {
//                for (index in it.indices) { //横屏
//                    if (index / 2 == 0) {
//                        viewModel.bannerList.add(it[index])
//                    } else {
//                        viewModel.bannerList2.add(it[index])
//                    }
//                }
//            }
//
//            binding.homeBanner.setDatas(viewModel.bannerList)
//            if (!isStarted) {
//                isStarted = true
//                binding.homeBanner.start()
//            }
//            binding.homeBanner.start() //开始轮播
//
////           bannerAdapter.notifyDataSetChanged()
//        }
//    }
}