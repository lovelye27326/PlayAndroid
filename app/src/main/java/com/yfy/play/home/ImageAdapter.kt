package com.yfy.play.home

import androidx.fragment.app.Fragment
import com.yfy.GlideUtils
import com.yfy.core.util.*
import com.yfy.model.room.entity.BannerBean
import com.yfy.play.R
import com.yfy.play.article.ArticleActivity
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder


/**
 * 描述：自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 *
 */
open class ImageAdapter(private var fragment: Fragment?) : BannerImageAdapter<BannerBean>(null) {
//    init { //在自定义的BannerViewHolder里才可以用init方法设点击事件，适配器这里设置会发生NPE
//        // 设置轮播图的点击事件监听器
//        viewHolder.imageView.setOnClickListener {
//            if (!ActivityUtil.getTopActivityOrApp().checkNetworkAvailable()) {
//                showToast(ActivityUtil.getTopActivityOrApp().getString(R.string.no_network))
//                return@setOnClickListener
//            }
//            if (it.getTag(R.id.tag_pos_key) != null) {
//                val titleUrl = it.getTag(R.id.tag_position_key) as String
//                val title = titleUrl.split("|")[1]
//                val url = titleUrl.split("|")[0]
//                ArticleActivity.actionStart(
//                    fragment?.requireActivity() ?: ActivityUtil.getTopActivityOrApp(),
//                    title.trim(),
//                    url
//                )
//            }
//        }
//    }

    override fun onBindView(
        holder: BannerImageHolder?,
        data: BannerBean?,
        position: Int,
        size: Int
    ) {
        holder?.imageView?.apply {
            val localPath = data?.filePath.ifNullOrBlank { "" } //先判断本地缓存
            val path =
                if (Validators[String::class].validate(localPath)) localPath else data?.imagePath.ifNullOrBlank { "https://psstatic.cdn.bcebos.com/video/wiseindex/aa6eef91f8b5b1a33b454c401_1660835115000.png" }
            GlideUtils.loadImgFrg(
                fragment,
                path,
                R.mipmap.default_banner,
                this
            )
//            val url = data?.url.ifNullOrBlank { "https:\\www.baidu.com" }
//            val title = data?.title.ifNullOrBlank { "" }

//            setOnClickListener { //点击事件
//                if (!ActivityUtil.getTopActivityOrApp().checkNetworkAvailable()) {
//                    showToast(ActivityUtil.getTopActivityOrApp().getString(R.string.no_network))
//                    return@setOnClickListener
//                }
//                ArticleActivity.actionStart(
//                    fragment?.requireActivity() ?: ActivityUtil.getTopActivityOrApp(),
//                    title,
//                    url
//                )
//            }
        }
    }

    fun clean() {
        if (fragment != null) fragment = null
    }
}

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
