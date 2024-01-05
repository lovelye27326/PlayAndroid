package com.yfy.play.home

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yfy.GlideUtils
import com.yfy.core.util.ActivityUtil
import com.yfy.core.util.checkNetworkAvailable
import com.yfy.core.util.showShortToast
import com.yfy.core.util.showToast
import com.yfy.model.room.entity.BannerBean
import com.yfy.play.R
import com.yfy.play.article.ArticleActivity
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder


/**
 * 描述：自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 *
 */
//open class ImageAdapter(private val mContext: Context, mData: List<BannerBean>) :
//    BannerAdapter<BannerBean?, ImageAdapter.BannerViewHolder?>(mData) {
//    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
//    override fun onCreateHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): BannerViewHolder {
//        val imageView = ImageView(parent.context).apply {
//            //注意，必须设置为match_parent，这个是viewpager2强制要求的
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            scaleType = ImageView.ScaleType.CENTER_CROP
//        }
//
//        return BannerViewHolder(imageView)
//    }
//
//    class BannerViewHolder(view: ImageView) :
//        RecyclerView.ViewHolder(view) {
//        var imageView: ImageView = view
//    }
//
//    override fun onBindView(
//        holder: BannerViewHolder?,
//        data: BannerBean?,
//        position: Int,
//        size: Int
//    ) {
//        holder?.imageView?.apply {
//            Glide.with(mContext)
//                .load(if (data?.filePath == null) data?.imagePath else data.filePath)
//                .into(this)
//            setOnClickListener {
//                if (!mContext.checkNetworkAvailable()) {
//                    mContext.showShortToast(mContext.getString(R.string.no_network))
//                    return@setOnClickListener
//                }
//                ArticleActivity.actionStart(
//                    mContext,
//                    data?.title ?: "",
//                    data?.url ?: "www.baidu.com"
//                )
//            }
//        }
//    }
//}

open class ImageAdapter(private var fragment: Fragment?) : BannerImageAdapter<BannerBean>(null) {
    override fun onBindView(
        holder: BannerImageHolder?,
        data: BannerBean?,
        position: Int,
        size: Int
    ) {
        holder?.imageView?.apply {
            GlideUtils.loadImgFrg(
                fragment,
                data?.url ?: "www.baidu.com",
                R.mipmap.default_banner,
                this
            )
            // 设置轮播图的点击事件监听器
            setOnClickListener {
                if (!ActivityUtil.getTopActivityOrApp().checkNetworkAvailable()) {
                    showToast(ActivityUtil.getTopActivityOrApp().getString(R.string.no_network))
                    return@setOnClickListener
                }
                ArticleActivity.actionStart(
                     fragment?.requireActivity()?: ActivityUtil.getTopActivityOrApp(),
                    data?.title ?: "",
                    data?.url ?: "www.baidu.com"
                )
            }
        }
    }

    fun clean() {
        if (fragment != null) fragment = null
    }
}