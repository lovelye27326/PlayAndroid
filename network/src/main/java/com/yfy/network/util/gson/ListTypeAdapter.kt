package com.yfy.network.util.gson

import com.yfy.network.util.GsonUtil
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*

/**
 * 数据兼容
 * 接口数据适配
 *
 * 日期： 2023年11月27日 13:23
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
class ListTypeAdapter : JsonDeserializer<List<*>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<*> {
        return try {
            if (json?.isJsonArray == true) { //如果json是List数组类型数据，那么就正常将其转换为List数组；如果不是，那么就解析为空数组
                GsonUtil.getInstance().fromJson(json, typeOfT)
            } else {
                Collections.EMPTY_LIST
            }
        } catch (e: Exception) {
            //
            Collections.EMPTY_LIST
        }
    }
}