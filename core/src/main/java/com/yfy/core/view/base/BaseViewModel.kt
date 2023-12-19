package com.yfy.core.view.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 *  泛型VM基类
 *  描述：PlayAndroid
 *
 */
abstract class BaseViewModel<BaseData, Data, Key> : ViewModel() {

    val dataList = ArrayList<Data>()

    private val pageLiveData = MutableLiveData<Key>()

    val dataLiveData = Transformations.switchMap(pageLiveData) { page ->
        getData(page)
    }

    abstract fun getData(page: Key): LiveData<Result<BaseData>>

    fun getDataList(page: Key) {
        pageLiveData.value = page!!
    }

}