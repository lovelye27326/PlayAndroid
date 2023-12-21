package com.yfy.play.article

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.yfy.core.Play
import com.yfy.core.util.*
import com.yfy.core.view.base.BaseRecyclerAdapter
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.entity.Article
import com.yfy.model.room.entity.HISTORY
import com.yfy.play.R
import com.yfy.play.article.collect.CollectRepository
import com.yfy.play.article.collect.CollectRepositoryPoint
import com.yfy.play.databinding.AdapterArticleBinding
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class ArticleAdapter(
    private val articleList: ArrayList<Article>,
    private val isShowCollect: Boolean = true,
    private val fragment: Fragment? = null
) : BaseRecyclerAdapter<AdapterArticleBinding>(), CoroutineScope by MainScope() {
//    init {
//
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerHolder<AdapterArticleBinding> {
        val binding =
            AdapterArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseRecyclerHolder(binding)
    }

    private fun setCollect(
        collectRepository: CollectRepository,
        t: Article,
        articleTvCollect: ImageView
    ) {
        launch(Dispatchers.IO) {
            val articleDao = PlayDatabase.getDatabase(Util.getApp()).browseHistoryDao()
            if (!t.collect) {
                val cancelCollects = collectRepository.cancelCollects(t.id)
                if (cancelCollects.errorCode == 0) {
                    withContext(Dispatchers.Main) {
                        articleTvCollect.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        showToast(
                            Util.getApp().getString(R.string.collection_cancelled_successfully)
                        )
                        articleDao.update(t)
                    }
                } else {
                    showToast(Util.getApp().getString(R.string.failed_to_cancel_collection))
                }
            } else {
                val toCollects = collectRepository.toCollects(t.id)
                if (toCollects.errorCode == 0) {
                    withContext(Dispatchers.Main) {
                        articleTvCollect.setImageResource(R.drawable.ic_favorite_black_24dp)
                        showToast(Util.getApp().getString(R.string.collection_successful))
                        articleDao.update(t)
                    }
                } else {
                    showToast(Util.getApp().getString(R.string.collection_failed))
                }
            }
        }
    }

    override fun onBaseBindViewHolder(position: Int, binding: AdapterArticleBinding) {
        val data = articleList[position]
        val collectRepository = EntryPointAccessors.fromApplication(
            Util.getApp(),
            CollectRepositoryPoint::class.java
        ).collectRepository()
        binding.apply {
            if (!TextUtils.isEmpty(data.title))
                articleTvTitle.text = getHtmlText(data.title)
            articleTvChapterName.text = data.superChapterName
            articleTvAuthor.text =
                if (TextUtils.isEmpty(data.author)) data.shareUser else data.author
            articleTvTime.text = data.niceShareDate
            if (!TextUtils.isEmpty(data.envelopePic)) {
                articleIvImg.visibility = VISIBLE
                if (fragment == null) {
                    Glide.with(binding.root.context).load(data.envelopePic).into(articleIvImg)
                } else {
                    Glide.with(fragment).load(data.envelopePic).into(articleIvImg)
                }
            } else {
                articleIvImg.visibility = GONE
            }
            articleTvTop.isVisible = data.type > 0
            articleTvNew.isVisible = data.fresh

            articleIvCollect.isVisible = isShowCollect
            if (data.collect) {
                articleIvCollect.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                articleIvCollect.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
            launch {
                Play.isLogin().collectLatest {
                    articleIvCollect.isVisible = it
                }
            }
            articleIvCollect.setSafeListener {
                if (Play.isLoginResult()) { //点击事件里不能在监听Play.isLogin().collectLatest结果里调用setCollect，每次进入收藏页会一直吐司提示
                    if (Util.getApp().checkNetworkAvailable()) {
                        data.collect = !data.collect
                        setCollect(collectRepository, data, articleIvCollect)
                    } else {
                        showToast(Util.getApp().getString(R.string.no_network))
                    }
                } else {
                    showToast(Util.getApp().getString(R.string.not_currently_logged_in))
                }
            }
            articleLlItem.setOnClickListener {
                if (!Util.getApp().checkNetworkAvailable()) {
                    showToast(Util.getApp().getString(R.string.no_network))
                    return@setOnClickListener
                }
                ArticleActivity.actionStart(it.context, data)
                val browseHistoryDao = PlayDatabase.getDatabase(Util.getApp()).browseHistoryDao()
                launch(Dispatchers.IO) {
                    if (browseHistoryDao.getArticle(data.id, HISTORY) == null) {
                        data.localType = HISTORY
                        data.desc = ""
                        browseHistoryDao.insert(data)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

}
