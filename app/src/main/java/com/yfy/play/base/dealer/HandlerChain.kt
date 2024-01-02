package com.yfy.play.base.dealer

import com.yfy.core.util.isInitialed
import com.yfy.core.util.releasableNotNull
import com.yfy.core.util.release


/**
 * 责任链对象
 */
class HandlerChain<I : Any, O : Any>(
    private val handlers: MutableList<Handler<I, O>>?,
    private val index: Int = 0
) : Handler.Chain<I, O> {
    private var nextChain by releasableNotNull<HandlerChain<I, O>>()

    override fun next(data: I): O? {
        if (index > (handlers?.size ?: 0) - 1) return handleNull(data)
        nextChain = HandlerChain(
            handlers,
            index + 1
        )
        return handlers?.get(index)?.handle(data, nextChain)
    }


    private fun handleNull(data: I): O {
        val handler = EmptyChain()
        val out = try {
            EmptyHandler().handle(data, handler) as O //强转，一般输入I、输出O的类型是一致的
        } catch (exception: Exception) {
            data as O
        }
        return out
    }

    fun clear() {
        if (::nextChain.isInitialed()) {
            nextChain.clear()
            ::nextChain.release()
        }
    }
}