package com.yfy.play.profile.rank.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yfy.core.util.DateUtils
import com.yfy.core.view.base.BaseRecyclerAdapter
import com.yfy.model.model.Ranks
import com.yfy.play.R
import com.yfy.play.databinding.AdapterRankBinding

class UserRankAdapter(private val context: Context, private val rankList: ArrayList<Ranks>) :
    BaseRecyclerAdapter<AdapterRankBinding>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerHolder<AdapterRankBinding> {
        val binding = AdapterRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseRecyclerHolder(binding)
    }

    override fun getItemCount(): Int {
        return rankList.size
    }

    override fun onBaseBindViewHolder(position: Int, binding: AdapterRankBinding) {
        val data = rankList[position]
        binding.apply {
            val coin = if (data.coinCount > 0) {
                "+${data.coinCount}"
            } else {
                "${data.coinCount}"
            }
            rankAdTvUsername.text = data.userName
            rankAdTvRank.text = context.getString(R.string.add_reason, data.reason)
            rankAdTvCoinCount.text = context.getString(R.string.rank, coin)
            rankAdTvTime.text = DateUtils.getDateString(context, data.date)
        }
    }

}
