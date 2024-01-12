package com.yfy.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.reflect.KProperty

/**
 *
 * 异步获取数据
 * [getData] [readBooleanFlow] [readFloatFlow] [readIntFlow] [readLongFlow] [readStringFlow]
 * 同步获取数据
 * [getSyncData] [readBooleanData] [readFloatData] [readIntData] [readLongData] [readStringData]
 *
 * 异步写入数据
 * [putData] [saveBooleanData] [saveFloatData] [saveIntData] [saveLongData] [saveStringData]
 * 同步写入数据
 * [putSyncData] [saveSyncBooleanData] [saveSyncFloatData] [saveSyncIntData] [saveSyncLongData] [saveSyncStringData]
 *
 * 异步清除数据
 * [clear]
 * 同步清除数据
 * [clearSync]
 *
 * 描述：DataStore 工具类
 *
 * Kotlin DataStore 的 data 属性返回一个 Flow，用于观察存储中的数据变化。当你从 DataStore 中收集数据时，Flow 会保持活跃状态，以便它可以发出数据存储中的任何更新。
Flow 不会在收集完成后自动关闭，因为它是设计来提供持续的数据更新的。如果你想在某个条件满足后停止收集数据，你可以使用 Flow 的转换操作符，比如 takeWhile 或 first 来只收集需要的数据。
例如，如果你想收集一次数据然后停止，你可以这样做：
kotlin
val dataStoreValue = dataStore.data
.first() // 只获取一次数据，然后流会完成
如果你想在满足特定条件后停止收集，可以使用 takeWhile：
kotlin
val dataStoreValue = dataStore.data
.takeWhile { condition } // 当条件不再满足时，流会完成
.collect { value ->
// 处理值
}
在使用 collect 时，你需要在合适的时机取消协程的执行，这通常是在 ViewModel 的 onCleared 方法中或者在你的 UI 控制器（如 Activity 或 Fragment）的 onDestroy 方法中。例如：
kotlin
viewModelScope.launch {
dataStore.data.collect { value ->
// 处理数据
}
}

override fun onCleared() {
super.onCleared()
viewModelScope.cancel() // 取消所有协程
}
在 UI 控制器中，你可能会使用 lifecycleScope：
kotlin
lifecycleScope.launchWhenStarted {
dataStore.data.collect { value ->
// 处理数据
}
}

override fun onDestroy() {
super.onDestroy()
lifecycleScope.cancel() // 取消所有协程
}
确保在不再需要收集数据时取消协程，以避免内存泄漏。
 *
 */

var Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PlayAndroidDataStore")

private operator fun Any.setValue( //by preferencesDataStore调的PreferenceDataStoreSingletonDelegate缺setValue方法
    context: Context,
    property: KProperty<*>,
    dataStore: DataStore<Preferences>
) {

}


object DataStoreUtils : DataStore<Preferences> {
    private const val TAG = "DataStoreUtils"
    private var dataStorePre by releasableNotNull<DataStore<Preferences>>()

    /**
     * init Context
     * @param context Context
     */
    fun init(context: Context): DataStoreUtils {
        dataStorePre = context.dataStore
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <U> getSyncData(key: String, default: U): U {
        val res = when (default) {
            is Long -> readLongData(key, default)
            is String -> readStringData(key, default)
            is Int -> readIntData(key, default)
            is Boolean -> readBooleanData(key, default)
            is Float -> readFloatData(key, default)
            else -> throw IllegalArgumentException("This type can not be saved into DataStore")
        }
        return res as U
    }

    @Suppress("UNCHECKED_CAST")
    fun <U> getData(key: String, default: U): Flow<U> {
        val data = when (default) {
            is Long -> if (::dataStorePre.isInitialed()) readLongFlow(
                dataStorePre,
                key,
                default
            ) else flow { emit(default) }
            is String -> if (::dataStorePre.isInitialed()) readStringFlow(
                dataStorePre,
                key,
                default
            ) else flow { emit(default) }
            is Int -> readIntFlow(key, default)
            is Boolean -> readBooleanFlow(key, default)
            is Float -> readFloatFlow(key, default)
            else -> throw IllegalArgumentException("This type can not be saved into DataStore")
        }
        return data as Flow<U>
    }

    suspend fun <U> putData(key: String, value: U) {
        when (value) {
            is Long -> if (::dataStorePre.isInitialed()) saveLongData(dataStorePre, key, value)
            is String -> if (::dataStorePre.isInitialed()) saveStringData(dataStorePre, key, value)
            is Int -> saveIntData(key, value)
            is Boolean -> saveBooleanData(key, value)
            is Float -> saveFloatData(key, value)
            else -> throw IllegalArgumentException("This type can not be saved into DataStore")
        }
    }

    fun <U> putSyncData(key: String, value: U) {
        when (value) {
            is Long -> saveSyncLongData(key, value)
            is String -> saveSyncStringData(key, value)
            is Int -> saveSyncIntData(key, value)
            is Boolean -> saveSyncBooleanData(key, value)
            is Float -> saveSyncFloatData(key, value)
            else -> throw IllegalArgumentException("This type can not be saved into DataStore")
        }
    }

    fun readBooleanFlow(key: String, default: Boolean = false): Flow<Boolean> =
        if (::dataStorePre.isInitialed()) dataStorePre.data
            .catch {
                //当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
                //但是如果是其他的异常，最好将它抛出去，不要隐藏问题
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[booleanPreferencesKey(key)] ?: default
            } else flow { emit(default) }


    /**
     * Kotlin的Flow<T>.first(predicate: suspend (T) -> Boolean): T函数用于从Flow中获取第一个满足给定predicate函数的元素。
     * 这个函数是suspend函数，这意味着它需要在协程中调用。

    以下是一个使用示例：

    kotlin
    import kotlinx.coroutines.flow.*

    fun main() = runBlocking<Unit> {
    // 创建一个Flow
    val numbers = flow {
    emit(1)
    emit(2)
    emit(3)
    emit(4)
    emit(5)
    }

    // 定义一个predicate函数，检查元素是否是偶数
    val isEven: suspend (Int) -> Boolean = { it % 2 == 0 }

    // 使用first函数获取Flow中第一个偶数
    val firstEvenNumber = numbers.first(isEven)

    println("First even number: $firstEvenNumber") // 输出：First even number: 2
    }
    在这个例子中，我们创建了一个包含一系列整数的Flow。然后我们定义了一个predicate函数isEven，它检查一个整数是否是偶数。最后，
    我们使用first函数和这个predicate函数来获取Flow中的第一个偶数，并打印出来。如果Flow中没有元素满足predicate函数，
    那么first函数会抛出NoSuchElementException异常。
     */
    fun readBooleanData(key: String, default: Boolean = false): Boolean {
        var value = false
        runBlocking {
            val isCondition: suspend (Preferences) -> Boolean = {
                value = it[booleanPreferencesKey(key)] ?: default
                true //总返回真，结束流收集
            }
            if (::dataStorePre.isInitialed())
                dataStorePre.data.first(isCondition)
        }
        return value
    }

    /**
     * 转换流Flow<Preferences>类型为Flow<Int>类型，再在使用的地方如HomeRepository里通过first操作符取出后自动关闭流，避免泄漏
     *
     * Kotlin的Flow<T>.map函数用于对Flow中的每个元素应用一个转换函数，并生成一个新的Flow，其中包含转换后的元素。
     * 这个函数是一个中间操作符，这意味着它不会立即执行任何计算，而是在收集时按需应用转换。
     *
     * Kotlin DataStore中的数据读取操作返回的Flow通常是冷流（Cold Flow）。

    在Kotlin中，冷流是指每次有新的收集者（如collect函数）时，都会重新执行整个流的生成逻辑。
    对于DataStore来说，这意味着每次启动一个新的收集流程时，它会从DataStore中查询最新的数据，并将这些数据作为流的一部分发送给收集者。
    这是因为DataStore的设计目标是提供一种持久化的、键值对的数据存储方式，其数据通常不会自动更新。当你订阅一个从DataStore获取数据的Flow时，
    你实际上是在请求当前存储在DataStore中的最新数据，而不是持续接收数据变化的通知。
    如果你需要一个能够自动推送数据更新的热流（Hot Flow），你可能需要结合使用其他的Kotlin协程库功能，
    如StateFlow或SharedFlow。你可以创建一个监听DataStore变化的回调，并在数据发生变化时更新相应的StateFlow或SharedFlow，这样其他部分的代码可以通过订阅这个热流来实时获取数据更新。

    另外,
    Kotlin DataStore中的数据读取Flow本身不会自动关闭收集。当你调用Flow的collect函数来获取和处理数据时，你需要自己管理收集的生命周期。

    通常情况下，你应在某种可以控制其生命周期的范围内（如协程作用域、ViewModel、LifecycleOwner等）收集Flow。这样，当这个范围结束或者被销毁时，相关的协程任务也会被取消，包括对Flow的收集。

    例如，在ViewModel中，你可以如下所示收集DataStore的Flow：

    kotlin
    class MyViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    private val _myData = MutableStateFlow("")
    val myData: StateFlow<String> get() = _myData

    init {
    viewModelScope.launch {
    dataStore.data
    .catch { exception ->
    if (exception is IOException) {
    // Handle IOExceptions here
    }
    emit(emptyPreferences())
    }
    .map { preferences ->
    preferences.myKey ?: ""
    }
    .onEach { newValue ->
    _myData.value = newValue
    }
    .launchIn(this)
    }
    }
    }
    在这个例子中，dataStore.data返回一个Flow，我们在ViewModel的生命周期范围内（通过viewModelScope）收集这个Flow。当ViewModel被清理时，由于launchIn函数的作用，对Flow的收集也会自动取消。

    总的来说，虽然DataStore的Flow本身不会自动关闭收集，但通过在适当的生命周期范围内管理Flow的收集，你可以确保在不需要数据时能够正确地释放资源

     */
    fun readIntFlow(key: String, default: Int = 0): Flow<Int> =
        if (::dataStorePre.isInitialed()) dataStorePre.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[intPreferencesKey(key)] ?: default
            } else flow { emit(default) }

    fun readIntData(key: String, default: Int = 0): Int {
        var value = 0
        runBlocking {
            if (::dataStorePre.isInitialed())
                dataStorePre.data.first {
                    value = it[intPreferencesKey(key)] ?: default
                    true
                }
        }
        return value
    }

    fun readStringFlow(
        dataStore: DataStore<Preferences>,
        key: String,
        default: String = ""
    ): Flow<String> =
        dataStore.data
            .catch {
                if (it is IOException) {
                    LogUtil.e(TAG, "readStringFlow ${it.message}")
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[stringPreferencesKey(key)] ?: default
            }

    fun readStringData(key: String, default: String = ""): String {
        var value = ""
        runBlocking {
            if (::dataStorePre.isInitialed())
                dataStorePre.data.first {
                    value = it[stringPreferencesKey(key)] ?: default
                    true
                }
        }
        return value
    }

    fun readFloatFlow(key: String, default: Float = 0f): Flow<Float> =
        if (::dataStorePre.isInitialed()) dataStorePre.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[floatPreferencesKey(key)] ?: default
            } else flow { emit(default) }

    fun readFloatData(key: String, default: Float = 0f): Float {
        var value = 0f
        runBlocking {
            if (::dataStorePre.isInitialed())
                dataStorePre.data.first {
                    value = it[floatPreferencesKey(key)] ?: default
                    true
                }
        }
        return value
    }

    fun readLongFlow(
        dataStore: DataStore<Preferences>,
        key: String,
        default: Long = 0L
    ): Flow<Long> =
        dataStore.data
            .catch {
                if (it is IOException) {
                    LogUtil.e(TAG, "readLongFlow ${it.message}")
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[longPreferencesKey(key)] ?: default
            }


    fun readLongData(key: String, default: Long = 0L): Long {
        var value = 0L
        runBlocking {
            if (::dataStorePre.isInitialed())
                dataStorePre.data.first {
                    value = it[longPreferencesKey(key)] ?: default
                    true
                }
        }
        return value
    }

    suspend fun saveBooleanData(key: String, value: Boolean) {
        if (::dataStorePre.isInitialed()) dataStorePre.edit { mutablePreferences ->
            mutablePreferences[booleanPreferencesKey(key)] = value
        }
    }

    fun saveSyncBooleanData(key: String, value: Boolean) =
        runBlocking { saveBooleanData(key, value) }

    suspend fun saveIntData(key: String, value: Int) {
        dataStorePre.edit { mutablePreferences ->
            mutablePreferences[intPreferencesKey(key)] = value
        }
    }

    fun saveSyncIntData(key: String, value: Int) = runBlocking { saveIntData(key, value) }

    suspend fun saveStringData(dataStore: DataStore<Preferences>, key: String, value: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[stringPreferencesKey(key)] = value
        }
    }

    fun saveSyncStringData(key: String, value: String) =
        runBlocking { saveStringData(dataStorePre, key, value) }

    suspend fun saveFloatData(key: String, value: Float) {
        dataStorePre.edit { mutablePreferences ->
            mutablePreferences[floatPreferencesKey(key)] = value
        }
    }

    fun saveSyncFloatData(key: String, value: Float) = runBlocking { saveFloatData(key, value) }

    suspend fun saveLongData(dataStore: DataStore<Preferences>, key: String, value: Long) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[longPreferencesKey(key)] = value
        }
    }

    private fun saveSyncLongData(key: String, value: Long) =
        runBlocking { saveLongData(dataStorePre, key, value) }

    suspend fun clear() {
        dataStorePre.edit {
            it.clear()
        }
    }

    fun clearSync() {
        runBlocking {
            dataStorePre.edit {
                it.clear()
            }
        }
    }

    fun clean() {
        if (::dataStorePre.isInitialed()) {
            ::dataStorePre.release()
        }
    }

    override val data: Flow<Preferences>
        get() = this.data //对应getData

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        return updateData {
            it.toMutablePreferences().apply { transform(this) }
        }
    }

}