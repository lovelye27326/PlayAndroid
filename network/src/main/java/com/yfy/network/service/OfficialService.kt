package com.yfy.network.service

import com.yfy.model.model.ArticleList
import com.yfy.model.model.BaseModel
import com.yfy.model.room.entity.ProjectClassify
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 文章
 *
 */
interface OfficialService {

    @GET("wxarticle/chapters/json")
    suspend fun getWxArticleTree(): BaseModel<List<ProjectClassify>>

    @GET("wxarticle/list/{cid}/{page}/json")
    suspend fun getWxArticle(@Path("page") page: Int, @Path("cid") cid: Int): BaseModel<ArticleList>

}