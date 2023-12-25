package com.yfy.play.base

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yfy.network.base.ServiceCreator
import com.yfy.network.service.LoginService
import com.yfy.core.util.GsonUtils
import com.yfy.core.util.dataStore
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
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Inject
//    lateinit var application: App

    @Singleton
    @Provides
    fun providerRetrofit(): Retrofit {
        return Retrofit.Builder().client(ServiceCreator.okHttpClient)
            .baseUrl(ServiceCreator.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
            .build()
    }


    @Singleton
    @Provides
    fun providerLoginService(retrofit: Retrofit): LoginService {  //通过函数参数传入retrofit
        return retrofit.create(LoginService::class.java)
    }

    @Singleton
    @Provides
    fun providerGetLoginRepository(service: LoginService): GetLoginRepository { //通过函数参数传入service
        return GetLoginRepository(service)
    }

    @Singleton
    @Provides
    fun providerRegisterRepository(service: LoginService): GetRegisterRepository {
        return GetRegisterRepository(service)
    }

    @Singleton
    @Provides
    fun providerLoginUseCase(
        getLoginRepository: GetLoginRepository,
    ): LoginUseCase {
        return LoginUseCase(getLoginRepository)
    }


    @Singleton
    @Provides
    fun providerRegisterUseCase(
        getRegisterRepository: GetRegisterRepository
    ): RegisterUseCase {
        return RegisterUseCase(getRegisterRepository)
    }


    @Singleton
    @Provides
    fun providerDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore //提供DataStore
    }

}