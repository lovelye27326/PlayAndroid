package com.yfy.play.base.dealer

/**
 * 责任链
 */
class DutyChain<I : Any, O : Any>(private val initData: I) {
    private var handlers: MutableList<Handler<I, O>>? = null
    private var handlerChain: HandlerChain<I, O>? = null

    fun addHandler(handler: Handler<I, O>) {
        if (handlers == null)
            handlers = mutableListOf()
        handlers?.add(handler)
    }

    fun execute(): O? {
        handlerChain = HandlerChain(handlers, 0)
        return handlerChain?.next(initData)
    }

    fun clear() {
        handlerChain?.clear()
        handlerChain = null
        handlers?.clear()
        handlers = null
    }
}


