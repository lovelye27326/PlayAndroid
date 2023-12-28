package com.yfy.play.base

import androidx.lifecycle.liveData
import com.yfy.core.util.LogUtil
import com.yfy.model.model.BaseModel

private const val TAG = "LiveDataUtils"

fun <T> liveDataModel(block: suspend () -> BaseModel<T>) =
    liveData {
        val result = try {
            val baseModel = block()
            if (baseModel.errorCode == 0) {
                val model = baseModel.data
                Result.success(model)
            } else {
                LogUtil.e(
                    TAG,
                    "fires: response status is ${baseModel.errorCode}  msg is ${baseModel.errorMsg}"
                )
                Result.failure(RuntimeException(baseModel.errorMsg))
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, "model ${e.message}")
            Result.failure(e)
        }
        emit(result)
    }

fun <T> liveDataFire(block: suspend () -> Result<T>) =
    liveData {
        val result = try {
            block()
        } catch (e: Exception) {
            LogUtil.e(TAG, "fire ${e.message}")
            Result.failure(e)
        }
        emit(result)
    }

