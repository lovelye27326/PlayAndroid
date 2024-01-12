package com.yfy.core.view.custom

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yfy.core.util.ReleasableNotNull
import com.yfy.core.util.isInitialed
import com.yfy.core.util.release
import java.lang.ref.WeakReference

/**
 *
 * 适配器
 */
class FragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private var mFragments by ReleasableNotNull<MutableList<WeakReference<Fragment>>>()
    private var mTitles by ReleasableNotNull<MutableList<String>>()

    init {
        mFragments = mutableListOf()
        mTitles = mutableListOf()
    }

    fun resetFragment(fragments: List<WeakReference<Fragment>>) {
        mFragments.addAll(fragments)
    }

    fun title(position: Int): String {
        return mTitles[position]
    }

    fun resetTitle(titles: List<String>) {
        mTitles.addAll(titles)
    }

    override fun getItemCount(): Int {
        return mFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        val weakRefFragment = mFragments[position]
        return if (weakRefFragment.get() != null) weakRefFragment.get()!! else Fragment()
    }


    fun clearFragments() {
        if (::mFragments.isInitialed()) {
            mFragments.clear()
            ::mFragments.release()
        }
        if (::mTitles.isInitialed()) {
            mTitles.clear()
            ::mTitles.release()
        }
    }
}