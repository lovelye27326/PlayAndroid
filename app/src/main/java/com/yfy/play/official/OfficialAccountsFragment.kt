package com.yfy.play.official

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.yfy.core.util.ReleasableNotNull
import com.yfy.core.util.getStatusBarHeight
import com.yfy.core.util.isInitialed
import com.yfy.core.util.release
import com.yfy.core.view.custom.FragmentAdapter
import com.yfy.play.databinding.FragmentOfficialAccountsBinding
import com.yfy.play.official.list.OfficialListFragment
import com.yfy.play.project.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class OfficialAccountsFragment : BaseTabFragment() {
    private val viewModel by viewModels<OfficialViewModel>()
    private var binding by ReleasableNotNull<FragmentOfficialAccountsBinding>()
    private var adapter by ReleasableNotNull<FragmentAdapter>()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): View {
        binding = FragmentOfficialAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun isHaveHeadMargin(): Boolean {
        return false
    }

    override fun initView() {
        adapter = FragmentAdapter(requireActivity())
        binding.apply {
            officialViewPager2.adapter = adapter
            officialTabLayout.addOnTabSelectedListener(this@OfficialAccountsFragment)
            TabLayoutMediator(officialTabLayout, officialViewPager2) { tab, position ->
                tab.text = adapter.title(position)
            }.attach()
            officialTabLayout.setPadding(0, context.getStatusBarHeight(), 0, 0)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        startLoading()
        setDataStatus(viewModel.dataLiveData) {
            val nameList = mutableListOf<String>()
            val viewList = mutableListOf<WeakReference<Fragment>>()
            it.forEach { project ->
                nameList.add(project.name)
                viewList.add(WeakReference(OfficialListFragment.newInstance(project.id)))
            }
            adapter.apply {
                resetTitle(nameList)
                resetFragment(viewList)
                notifyDataSetChanged()
            }
            binding.officialViewPager2.currentItem = viewModel.position
        }
    }

    override fun onTabPageSelected(position: Int) {
        viewModel.position = position
    }

    companion object {
        @JvmStatic
        fun newInstance() = OfficialAccountsFragment()
    }

    override fun onDestroy() {
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        super.onDestroy()
        if (::adapter.isInitialed()) {
            adapter.clearFragments()
            ::adapter.release()
        }
    }
}
