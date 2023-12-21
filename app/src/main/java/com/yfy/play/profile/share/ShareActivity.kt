package com.yfy.play.profile.share

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.yfy.core.Play
import com.yfy.model.model.CoinInfo
import com.yfy.model.model.ShareModel
import com.yfy.play.R
import com.yfy.play.article.ArticleAdapter
import com.yfy.play.databinding.ActivityShareBinding
import com.yfy.play.home.ArticleCollectBaseActivity
import com.yfy.play.profile.rank.user.UserRankActivity
import com.yfy.play.profile.share.add.AddShareActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val IS_MINE = "IS_MINE"
const val USER_ID = "USER_ID"

@AndroidEntryPoint
class ShareActivity : ArticleCollectBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityShareBinding
    private val viewModel by viewModels<ShareViewModel>()
    private var isMine: Boolean = true
    private var userId: Int = 0
    private lateinit var articleAdapter: ArticleAdapter
    private var page = 1

    override fun getLayoutView(): View {
        binding = ActivityShareBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData() {
        isMine = intent.getBooleanExtra(IS_MINE, true)
        userId = intent.getIntExtra(USER_ID, 0)
        if (!isMine) binding.shareTitleBar.setTitle(getString(R.string.author_share))
        if (isMine) {
            setDataStatus(viewModel.articleLiveData) {
                setArticleData(it)
            }
        } else {
            setDataStatus(viewModel.articleAndCidLiveData) {
                setArticleData(it)
            }
        }

        lifecycleScope.launch {
            Play.isLogin().collectLatest {
                if (it) {
                    binding.shareTitleBar.setRightText(getString(R.string.add))
                    binding.shareTitleBar.setRightTextOnClickListener {
                        AddShareActivity.actionStart(this@ShareActivity)
                    }
                }
            }
        }

        getArticleList()
    }

    private fun setArticleData(shareModel: ShareModel) {
        if (page == 1 && viewModel.articleList.size > 0) {
            viewModel.articleList.clear()
        }
        setUserInfo(shareModel.coinInfo)
        viewModel.articleList.addAll(shareModel.shareArticles.datas)
        if (viewModel.articleList.size == 0) {
            showNoContentView(getString(R.string.no_data))
        }
        articleAdapter.notifyItemInserted(viewModel.articleList.size)
    }

    private fun setUserInfo(coinInfo: CoinInfo) {
        binding.shareHeadLl.visibility = View.VISIBLE
        binding.shareTvName.text = coinInfo.username
        binding.shareTvRank.text =
            getString(R.string.man_info, coinInfo.level, coinInfo.rank, coinInfo.coinCount)
    }

    override fun initView() {
        binding.shareTvRank.setOnClickListener(this)
        binding.shareToTopRecyclerView.setRecyclerViewLayoutManager(true)
        articleAdapter = ArticleAdapter(
            viewModel.articleList
        )
        binding.shareToTopRecyclerView.setAdapter(articleAdapter)
        binding.shareToTopRecyclerView.onRefreshListener({
            page = 1
            getArticleList()
        }, {
            page++
            getArticleList()
        })
    }

    private fun getArticleList() {
        if (viewModel.articleList.size <= 0) startLoading()
        if (isMine) {
            viewModel.getArticleList(page)
        } else {
            viewModel.getArticleList(userId, page)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.shareTvRank -> {
                UserRankActivity.actionStart(this)
            }
        }
    }

    companion object {
        fun actionStart(context: Context, isMine: Boolean, userId: Int = 0) {
            val intent = Intent(context, ShareActivity::class.java).apply {
                putExtra(IS_MINE, isMine)
                putExtra(USER_ID, userId)
            }
            context.startActivity(intent)
        }
    }

}