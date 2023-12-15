package com.zj.model.model

/**
 *
 * 基类
 */
const val SUCCESS_CODE = 0
data class BaseModel<T>(
    val `data`: T,
    val errorCode: Int,
    val errorMsg: String
)

fun<T> BaseModel<T>?.isSuccess() = this?.errorCode == SUCCESS_CODE