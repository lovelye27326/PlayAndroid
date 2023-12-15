package com.zj.network.service

import com.zj.model.model.ArticleList
import com.zj.model.model.BaseModel
import com.zj.model.room.entity.Article
import com.zj.model.room.entity.BannerBean
import com.zj.model.room.entity.HotKey
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *  首页
 */
interface HomePageService {

    @GET("banner/json")
    suspend fun getBanner(): BaseModel<List<BannerBean>>

    @GET("article/top/json")
    suspend fun getTopArticle(): BaseModel<List<Article>>

    @GET("article/list/{a}/json")
    suspend fun getArticle(@Path("a") a: Int): BaseModel<ArticleList>

    @GET("hotkey/json")
    suspend fun getHotKey(): BaseModel<List<HotKey>>

    @POST("article/query/{page}/json")
    suspend fun getQueryArticleList(@Path("page") page: Int, @Query("k") k: String): BaseModel<ArticleList>

}