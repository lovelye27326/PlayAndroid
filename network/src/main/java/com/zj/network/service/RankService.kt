package com.zj.network.service

import com.zj.model.model.BaseModel
import com.zj.model.model.RankData
import com.zj.model.model.RankList
import com.zj.model.model.UserInfo
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 排名
 *
 */
interface RankService {

    @GET("coin/rank/{page}/json")
    suspend fun getRankList(@Path("page") page: Int): BaseModel<RankData>

    @GET("lg/coin/userinfo/json")
    suspend fun getUserInfo(): BaseModel<UserInfo>

    @GET("lg/coin/list/{page}/json")
    suspend fun getUserRank(@Path("page") page: Int): BaseModel<RankList>

}