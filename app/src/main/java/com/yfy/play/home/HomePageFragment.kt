package com.yfy.play.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.yfy.core.util.*
import com.yfy.core.util.ScreenUtils.dp2px
import com.yfy.play.R
import com.yfy.play.article.ArticleAdapter
import com.yfy.play.databinding.FragmentHomePageBinding
import com.yfy.play.home.almanac.AlmanacActivity
import com.yfy.play.home.search.SearchActivity
import com.yfy.play.main.MainActivity
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.transformer.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : ArticleCollectBaseFragment() {
    private val viewModel by viewModels<HomePageViewModel>()
    private var binding by releasableNotNull<FragmentHomePageBinding>()
    private var isStarted: Boolean = false

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
        getArticleList(true)
    }

    private lateinit var bannerAdapter: ImageAdapter

    //    private lateinit var bannerAdapter2: ImageAdapter
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
            }

            homeToTopRecyclerView.setRecyclerViewLayoutManager(true)
            articleAdapter = ArticleAdapter(viewModel.articleList, true, this@HomePageFragment)
            homeToTopRecyclerView.onRefreshListener({
                page = 1
                LogUtil.i("HomePageFrg", "refresh page = $page")
                getArticleList(true)
            }, {
                page++
                LogUtil.i("HomePageFrg", "loadMore page = $page")
                getArticleList(false)
            })
            homeToTopRecyclerView.setAdapter(articleAdapter)
        }
    }

    override fun initData() {
        startLoading()
        initBanner()
        setDataStatus(viewModel.articleLiveData, {
            if (viewModel.articleList.size > 0) loadFinished() //判断弱网情况下且已加载的articleList非空则显示加载结束
        }) {
            if (page == 1 && viewModel.articleList.size > 0) {
                viewModel.articleList.clear()
            }
            viewModel.articleList.addAll(it)
            articleAdapter.notifyItemInserted(it.size)
        }
        getArticleList(false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initBanner() {
        setDataStatus(viewModel.getBanner(), {
            if (viewModel.bannerList.size > 0) loadFinished() //判断弱网情况加载结束
        }) {
            val size = it.size
            LogUtil.i("HomePageFrg", "List size: $size")
            val main = activity as MainActivity
            if (viewModel.bannerList.size > 0)
                viewModel.bannerList.clear()
            if (viewModel.bannerList2.size > 0)
                viewModel.bannerList2.clear()
            if (main.isPort) {
                viewModel.bannerList.addAll(it)
            } else {
                for (index in it.indices) { //横屏
                    if (index / 2 == 0) {
                        viewModel.bannerList.add(it[index])
                    } else {
                        viewModel.bannerList2.add(it[index])
                    }
                }
            }

            binding.homeBanner.setDatas(viewModel.bannerList)
            if (!isStarted) {
                isStarted = true
                binding.homeBanner.start()
            }
            binding.homeBanner.start() //开始轮播

//            bannerAdapter.notifyDataSetChanged()
        }

    }

    private fun getArticleList(isRefresh: Boolean) {
        viewModel.getArticleList(page, isRefresh)
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
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomePageFragment()
    }

}