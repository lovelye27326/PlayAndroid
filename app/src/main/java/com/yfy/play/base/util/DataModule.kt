package com.yfy.play.base.util

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *
 * Claude: @SingletonComponent注解用于定义单例的Hilt组件。这个组件会在应用启动时创建,并贯穿整个应用的生命周期。
@SingletonComponent 中可以使用 @Binds 注解来提供依赖注入的绑定。
例如:
kotlin
@SingletonComponent
interface AppComponent {

@Binds
fun bindAnalyticsService(impl: FirebaseAnalyticsService): AnalyticsService

}
这里使用 @Binds 将 FirebaseAnalyticsService 绑定到 AnalyticsService 接口。
其中:
- @Binds 表示这个是一个绑定方法,会把左边的实现类绑定到右边边的接口上(kotlin)。
- 方法的返回值是绑定的接口,参数是具体的实现类。
- 这样就可以通过接口来注入实现类了。
然后在需要依赖注入的地方:
kotlin
class AnalyticsManager @Inject constructor(
private val analyticsService: AnalyticsService
)
只需要声明依赖接口即可,实现类会被自动注入。
这种绑定方式可以减少模块之间的直接依赖,更方便修改和测试。
所以在Hilt的组件中,可以通过`@Binds`来声明依赖注入的绑定关系。
 */

@Module
@InstallIn(SingletonComponent::class) //@InstallIn: 这个注解用于@Module注解的类，指定这个模块安装在哪个Hilt组件中
abstract class DataModule {

    @Binds
    abstract fun bindPreferencesStorage(
        impl: DataStorePreferencesStorage
    ): PreferencesStorage

    //@Binds方式传入的是impl实现类，后期容易替换具体的impl实现类，相对@Provide更灵活
}
