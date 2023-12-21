package com.yfy.play.profile

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yfy.core.Play
import com.yfy.core.util.LogUtil
import com.yfy.core.view.base.BaseRecyclerAdapter
import com.yfy.play.R
import com.yfy.play.article.ArticleActivity
import com.yfy.play.article.collect.CollectListActivity
import com.yfy.play.base.util.ActivityRouter
import com.yfy.play.databinding.AdapterProfileBinding
import com.yfy.play.profile.history.BrowseHistoryActivity
import com.yfy.play.profile.rank.user.UserRankActivity
import com.yfy.play.profile.user.UserActivity

/**
 *
 * 个人简介
 * 描述：PlayAndroid
 *
 */
class ProfileAdapter(
    private val mContext: Context,
    private val profileItemList: ArrayList<ProfileItem>,
) : BaseRecyclerAdapter<AdapterProfileBinding>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerHolder<AdapterProfileBinding> {
        val binding =
            AdapterProfileBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return BaseRecyclerHolder(binding)
    }


    override fun onBaseBindViewHolder(position: Int, binding: AdapterProfileBinding) {
        val data = profileItemList[position]
        binding.apply {
            profileAdTvTitle.text = data.title
            profileAdIv.setImageResource(data.imgId)
            profileAdLlItem.setOnClickListener {
                val title = data.title
                LogUtil.i("ProfileAdapter", "title: $title")
                when (title) {
                    mContext.getString(R.string.mine_points) -> {
                        if (Play.isLoginResult()) {
                            UserRankActivity.actionStart(mContext)
                        } else {
                            ActivityRouter.showLoginActivity(mContext)
//                    LoginActivity.actionStart(mContext)
                        }
                    }
                    mContext.getString(R.string.my_collection) -> {
                        if (Play.isLoginResult()) {
                            CollectListActivity.actionStart(mContext)
                        } else {
                            ActivityRouter.showLoginActivity(mContext)
//                    LoginActivity.actionStart(mContext)
                        }
                    }
                    mContext.getString(R.string.mine_blog) -> {
                        ArticleActivity.actionStart(
                            mContext,
                            mContext.getString(R.string.mine_blog),
                            "https://zhujiang.blog.csdn.net/"
                        )
                    }
                    mContext.getString(R.string.browsing_history) -> {
                        BrowseHistoryActivity.actionStart(mContext)

                    }
                    mContext.getString(R.string.mine_nuggets) -> {
                        ArticleActivity.actionStart(
                            mContext,
                            mContext.getString(R.string.mine_nuggets),
                            "https://juejin.im/user/5c07e51de51d451de84324d5"
                        )
                    }
                    mContext.getString(R.string.github) -> {
                        ArticleActivity.actionStart(
                            mContext,
                            mContext.getString(R.string.mine_github),
                            "https://github.com/zhujiang521"
                        )
                    }
                    mContext.getString(R.string.about_me) -> {
                        UserActivity.actionStart(mContext)
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return profileItemList.size
    }

}

data class ProfileItem(var title: String, var imgId: Int)