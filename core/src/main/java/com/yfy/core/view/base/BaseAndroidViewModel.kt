package com.yfy.core.view.base

import androidx.lifecycle.*
import com.yfy.core.util.LogUtil

/**
 *
 * 基类
 */
abstract class BaseAndroidViewModel<BaseData, Data, Key> : ViewModel() {

    val dataList = ArrayList<Data>()

    private val pageLiveData = MutableLiveData<Key?>()

    val dataLiveData = Transformations.switchMap(pageLiveData) { page ->
        if (page != null) {
            getData(page)
        } else {
            liveDataFire {
                Result.failure(RuntimeException("response status is null"))
            }
        }
    }

    abstract fun getData(page: Key): LiveData<Result<BaseData>>

    fun getDataList(page: Key) {
        pageLiveData.value = page
    }


    private fun <T> liveDataFire(block: () -> Result<T>) =
        liveData {
            val result = try {
                block()
            } catch (e: Exception) {
                LogUtil.e("BaseAndroidViewModel", "fire $e")
                Result.failure(e)
            }
            emit(result)
        }
}