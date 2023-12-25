package com.yfy.play.base.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yfy.core.util.DataStoreUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 *  定义接口
 */
interface PreferencesStorage {
    suspend fun putStringData(key: String, data: String)
    suspend fun getStringData(key: String, default: String?): Flow<String>
    suspend fun putLongData(key: String, data: Long)
    suspend fun getLongData(key: String, default: Long?): Flow<Long>
}

class DataStorePreferencesStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesStorage {

    override suspend fun getStringData(key: String, default: String?): Flow<String> {
        return if (default == null) DataStoreUtils.readStringFlow(
            dataStore,
            key
        ) else DataStoreUtils.readStringFlow(dataStore, key, default)
    }

    override suspend fun putStringData(key: String, data: String) {
        // 储存字符串类型
        DataStoreUtils.saveStringData(dataStore, key, data)
    }

    override suspend fun putLongData(key: String, data: Long) {
        DataStoreUtils.saveLongData(dataStore, key, data)
    }

    override suspend fun getLongData(key: String, default: Long?): Flow<Long> {
        return if (default == null) DataStoreUtils.readLongFlow(
            dataStore,
            key
        ) else DataStoreUtils.readLongFlow(dataStore, key, default)
    }

}