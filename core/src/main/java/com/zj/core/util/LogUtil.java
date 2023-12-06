package com.zj.core.util;

import android.util.Log;


import com.zj.core.BuildConfig;

/**
 * 日志打印管理
 */
public class LogUtil {
  private static final boolean DEBUG_MODE = BuildConfig.IS_DEBUG_BUILD; //在 defaultConfig 块中定义的一个自定义的 build config 字段，
  // 在主工程里也可以用IS_DEBUG_BUILD来判断

  public static void v(String tag, String msg) {
    if (DEBUG_MODE) {
      Log.v(tag, msg);
    }
  }

  public static void v(Class<?> clazz, String msg) {
    v(clazz.getSimpleName(), msg);
  }

  public static void d(String tag, String msg) {
    if (DEBUG_MODE) {
      Log.d(tag, msg);
    }
  }


  public static void i(String msg) {
    if (DEBUG_MODE) {
      i("LogUtil", msg);
    }
  }


  public static void d(Class<?> clazz, String msg) {
    d(clazz.getSimpleName(), msg);
  }

  public static void i(String tag, String msg) {
    if (DEBUG_MODE) {
      Log.i(tag, msg);
    }
  }

  public static void i(String tag, Object msg) {
    if (DEBUG_MODE) {
      Log.i(tag, String.valueOf(msg));
    }
  }

  public static void i(Class<?> clazz, String msg) {
    i(clazz.getSimpleName(), msg);
  }

  public static void w(String tag, String msg) {
    if (DEBUG_MODE) {
      Log.w(tag, msg);
    }
  }

  public static void w(Class<?> clazz, String msg) {
    w(clazz.getSimpleName(), msg);
  }

  public static void e(String tag, String msg) {
    if (DEBUG_MODE) {
      Log.e(tag, msg);
    }
  }

  public static void e(String msg) {
    if (DEBUG_MODE) {
      e("LogUtil", msg);
    }
  }

  public static void e(Throwable e, String msg) {
    if (DEBUG_MODE) {
      e("LogUtil", msg + ", err = " + e.getMessage());
    }
  }

  public static void e(Class<?> clazz, String msg) {
    e(clazz.getSimpleName(), msg);
  }
}
