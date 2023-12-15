package com.zj.network.action

import com.zj.model.model.BaseModel

/**
 * 请求Action封装
 * 日期： 2023年12月15日 15:40
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
class RequestAction<T> {
    //开始请求
    var start: (() -> Unit)? = null
        private set

    //发起请求
    var request: (suspend () -> BaseModel<T>)? = null
        private set

    // 请求成功
    var success: ((T?) -> Unit)? = null
        private set

    //请求失政
    var error: ((String) -> Unit)? = null
        private set

    // 请求结束
    var finish: (() -> Unit)? = null
        private set

    fun request(block: suspend () -> BaseModel<T>) {
        request = block
    }

    fun start(block: () -> Unit) {
        start = block
    }

    fun success(block: (T?) -> Unit) {
        success = block
    }

    fun error(block: (String) -> Unit) {
        error = block
    }

    fun finish(block: () -> Unit) {
        finish = block
    }
}