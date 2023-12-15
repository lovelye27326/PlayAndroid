package com.zj.network.action;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface LoaderState {

    /**
     * 初始化中
     */
    int STATE_INITIALIZED = 0;

    /**
     * 加载中
     */
    int STATE_LOADING = 1;

    /**
     * 加载成功
     */
    int STATE_SUCCESS = 2;

    /**
     * 网络错误
     */
    int STATE_NET_ERROR = 3;

    /**
     * 数据源错误 : 接口或数据库出错
     */
    int STATE_SOURCE_ERROR = 4;

    @IntDef({STATE_INITIALIZED, STATE_LOADING, STATE_SUCCESS, STATE_NET_ERROR, STATE_SOURCE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {
    }

    /**
     * 获取当前数据状态
     *
     * @return 当前数据状态
     */
    @State int getState();


    /**
     * 设置数据状态
     *
     * @param state 数据状态
     */
    void setState(@State int state);

}
