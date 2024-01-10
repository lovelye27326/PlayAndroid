package com.yfy.play.base

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yfy.core.util.GsonUtils
import com.yfy.core.util.dataStore
import com.yfy.network.base.ServiceCreator
import com.yfy.network.service.HomePageService
import com.yfy.network.service.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
@InstallIn(SingletonComponent::class) //@InstallIn: 这个注解用于@Module注解的类，指定这个模块安装在哪个Hilt组件中，还有DataModule用来注入dataStore
object AppModule {

//    @Inject
//    lateinit var application: App

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().client(ServiceCreator.okHttpClient)
            .baseUrl(ServiceCreator.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
            .build()
    }


    @Singleton
    @Provides
    fun provideLoginService(retrofit: Retrofit): LoginService {  //通过函数参数传入retrofit
        return retrofit.create(LoginService::class.java)
    }

    @Singleton
    @Provides
    fun provideHomePageService(retrofit: Retrofit): HomePageService {  //通过函数参数传入retrofit
        return retrofit.create(HomePageService::class.java)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(service: LoginService): LoginRepository { //通过函数参数传入service
        return LoginRepository(service)
    }

    @Singleton
    @Provides
    fun provideRegisterRepository(service: LoginService): RegisterRepository {
        return RegisterRepository(service)
    }

    @Singleton
    @Provides
    fun provideHomeBannerRepository(service: HomePageService): HomeBannerRepository {
        return HomeBannerRepository(service)
    }

    @Singleton
    @Provides
    fun provideHomeTopArticleListRepository(service: HomePageService): HomeTopArticleListRepository {
        return HomeTopArticleListRepository(service)
    }


    @Singleton
    @Provides
    fun provideHomeCommonArticleListRepository(service: HomePageService): HomeCommonArticleListRepository {
        return HomeCommonArticleListRepository(service)
    }


    @Singleton
    @Provides
    fun provideLoginUseCase(
        loginRepository: LoginRepository,
    ): LoginUseCase {
        return LoginUseCase(loginRepository)
    }


    @Singleton
    @Provides
    fun provideRegisterUseCase(
        registerRepository: RegisterRepository
    ): RegisterUseCase {
        return RegisterUseCase(registerRepository)
    }

    @Singleton
    @Provides
    fun provideHomeBannerUseCase(
        homeBannerRepository: HomeBannerRepository,
    ): HomeBannerUseCase {
        return HomeBannerUseCase(homeBannerRepository)
    }


    @Singleton
    @Provides
    fun provideHomeTopArticleListUseCase(
        homeTopArticleListRepository: HomeTopArticleListRepository,
    ): HomeTopArticleListUseCase {
        return HomeTopArticleListUseCase(homeTopArticleListRepository)
    }


    @Singleton
    @Provides
    fun provideHomeCommonArticleListUseCase(
        homeCommonArticleListRepository: HomeCommonArticleListRepository,
    ): HomeCommonArticleListUseCase {
        return HomeCommonArticleListUseCase(homeCommonArticleListRepository)
    }


    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore //提供DataStore
    }

}