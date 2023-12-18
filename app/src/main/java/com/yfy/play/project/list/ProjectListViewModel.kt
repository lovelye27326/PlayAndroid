package com.yfy.play.project.list

import androidx.lifecycle.LiveData
import com.yfy.core.view.base.BaseAndroidViewModel
import com.yfy.model.pojo.QueryArticle
import com.yfy.model.room.entity.Article
import com.yfy.play.project.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 工程列表
 * 描述：PlayAndroid
 *
 */
@HiltViewModel
class ProjectListViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : BaseAndroidViewModel<List<Article>, Article, QueryArticle>() {

    override fun getData(page: QueryArticle): LiveData<Result<List<Article>>> {
        return projectRepository.getProject(page)
    }

}