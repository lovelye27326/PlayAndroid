package com.yfy.play.base.dealer

/**
 * 责任链处理输入输出
 */
interface Handler<I, O> {
    fun handle(data: I, chain: Chain<I, O>): O?

    interface Chain<I, O> {
        fun next(data: I): O?
    }
}

class EmptyHandler : Handler<Any, Any> {

    override fun handle(data: Any, chain: Handler.Chain<Any, Any>): Any {
        return data
    }
}

class EmptyChain: Handler.Chain<Any, Any> {
    override fun next(data: Any): Any {
        return data
    }

}