package com.zj.play.base

import com.zj.network.base.ServiceCreator
import com.zj.network.service.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * hilt 自定义app模块
 * 日期： 2023年12月08日 10:11
 * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *      _              _           _     _   ____  _             _ _
 *     / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *    / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *   / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  -- yfy
 *
 * You never know what you can do until you try !
 * ----------------------------------------------------------------
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Inject
//    lateinit var application: App

    @Singleton
    @Provides
    fun providerRetrofit(): Retrofit {
        return Retrofit.Builder().client(ServiceCreator.okHttpClient)
            .baseUrl(ServiceCreator.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providerLoginService(retrofit: Retrofit): LoginService {  //通过函数参数传入retrofit
        return retrofit.create(LoginService::class.java)
    }

    @Singleton
    @Provides
    fun providerGetLoginProjects(service: LoginService): GetLoginProjects { //通过函数参数传入service
        return GetLoginProjects(service)
    }

    @Singleton
    @Provides
    fun providerRegisterProjects(service: LoginService): GetRegisterProjects {
        return GetRegisterProjects(service)
    }

    @Singleton
    @Provides
    fun providerUserUseCase(
        getLoginProjects: GetLoginProjects,
        getRegisterProjects: GetRegisterProjects
    ): UserUseCase {
        return UserUseCase(getLoginProjects, getRegisterProjects)
    }


}