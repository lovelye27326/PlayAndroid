package com.yfy.network.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.yfy.core.util.LogUtil;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * @author jx on 2019/2/19.
 */
public class HandleException {

    public static String handleResponseError(Throwable t) {
        String msg;
        LogUtil.e("HandleException", "msg = " + t.getMessage());
        if (t instanceof UnknownHostException) {
            msg = "网络不可用";
        } else if (t instanceof SocketTimeoutException) {
            msg = "请求网络超时";
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            msg = convertStatusCode(httpException);
        } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException) {
            msg = "数据解析错误";
        } else {
            msg = t.getMessage();
        }
        return msg;
    }

    public static String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = "网络错误";
        }
        return msg;
    }
}
