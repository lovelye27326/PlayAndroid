package com.yfy.play.project

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
import com.yfy.play.databinding.FragmentProjectBinding
import com.yfy.play.project.list.ProjectListFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class ProjectFragment : BaseTabFragment() {
    private val viewModel by viewModels<ProjectViewModel>()
    private var adapter by ReleasableNotNull<FragmentAdapter>()
    private var binding by ReleasableNotNull<FragmentProjectBinding>()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun isHaveHeadMargin(): Boolean {
        return false
    }

    override fun initView() {
        adapter = FragmentAdapter(requireActivity())
        binding.apply {
            projectViewPager2.adapter = adapter
            projectTabLayout.addOnTabSelectedListener(this@ProjectFragment)
            TabLayoutMediator(projectTabLayout, projectViewPager2) { tab, position ->
                tab.text = adapter.title(position)
            }.attach()
            projectTabLayout.setPadding(0, context.getStatusBarHeight(), 0, 0)
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
                viewList.add(WeakReference(ProjectListFragment.newInstance(project.id)))
            }
            adapter.apply {
                resetTitle(nameList)
                resetFragment(viewList)
                notifyDataSetChanged()
            }
            binding.projectViewPager2.currentItem = viewModel.position
        }
    }

    override fun onTabPageSelected(position: Int) {
        viewModel.position = position
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

    companion object {
        @JvmStatic
        fun newInstance() = ProjectFragment()
    }
}
