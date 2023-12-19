package com.yfy.play.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.yfy.core.Play;
import com.yfy.core.util.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

//import io.reactivex.exceptions.UndeliverableException;
//import io.reactivex.plugins.RxJavaPlugins;

public class ScreenUtils {

  public static int dp2px(Context context, float dp) {
    if (context == null) {
      return -1;
    }
    return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
  }

  public static float px2dp(Context context, float px) {
    if (context == null) {
      return -1;
    }
    return px / context.getResources().getDisplayMetrics().density;
  }

  public static float dpToPxInt(Context context, float dp) {
    return dp2px(context, dp) + 0.5f;
  }

  public static float pxToDpCeilInt(Context context, float px) {
    return px2dp(context, px) + 0.5f;
  }


  public static int getScreenWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getScreenHeight(Context context) {
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * 获取屏幕密度
   */
  public static float getScreenDensity(Context context) {
    return context.getResources().getDisplayMetrics().density;
  }

  /**
   * 将sp值转换为px值，保证文字大小不变
   *
   * @param spValue （DisplayMetrics类中属性scaledDensity）
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * 关灯
   */
  public static void lightOff(Activity activity) {
    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
    lp.alpha = 0.7f;
    activity.getWindow().setAttributes(lp);
  }

  /**
   * 关灯
   */
  public static void lightOn(Activity activity) {
    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
    lp.alpha = 1f;
    activity.getWindow().setAttributes(lp);
  }

  //获取真实屏幕高度
  public static int getRealScreenHeight(Activity activity) {
    if (activity == null) {
      return 0;
    }
    Display display = activity.getWindowManager().getDefaultDisplay();
    int realHeight = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      final DisplayMetrics metrics = new DisplayMetrics();
      display.getRealMetrics(metrics);
      realHeight = metrics.heightPixels;
    } else {
      try {
        Method mGetRawH = Display.class.getMethod("getRawHeight");
        realHeight = (Integer) mGetRawH.invoke(display);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return realHeight;
  }

  /**
   * 获取可用屏幕高度，排除虚拟键
   *
   * @param context 上下文
   * @return 返回高度
   */
  public static int getContentHeight(Activity context) {
    return getRealScreenHeight(context) - getCurrentNavigationBarHeight(context);
  }

  public static int getCurrentNavigationBarHeight(Activity activity) {
    int navigationBarHeight = 0;
    Resources resources = activity.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0 && checkDeviceHasNavigationBar(activity) && isNavigationBarVisible(
        activity)) {
      navigationBarHeight = resources.getDimensionPixelSize(resourceId);
    }
    return navigationBarHeight;
  }

  private static boolean isNavigationBarVisible(Activity activity) {
    boolean show = false;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
      Point point = new Point();
      display.getRealSize(point);
      View decorView = activity.getWindow().getDecorView();
      Configuration conf = activity.getResources().getConfiguration();
      if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
        View contentView = decorView.findViewById(android.R.id.content);
        show = (point.x != contentView.getWidth());
      } else {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        show = (rect.bottom != point.y);
      }
    }
    return show;
  }

  private static boolean checkDeviceHasNavigationBar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      WindowManager windowManager = activity.getWindowManager();
      Display display = windowManager.getDefaultDisplay();
      DisplayMetrics realDisplayMetrics = new DisplayMetrics();
      display.getRealMetrics(realDisplayMetrics);
      int realHeight = realDisplayMetrics.heightPixels;
      int realWidth = realDisplayMetrics.widthPixels;
      DisplayMetrics displayMetrics = new DisplayMetrics();
      display.getMetrics(displayMetrics);
      int displayHeight = displayMetrics.heightPixels;
      int displayWidth = displayMetrics.widthPixels;
      return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    } else {
      boolean hasNavigationBar = false;
      Resources resources = activity.getResources();
      int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
      if (id > 0) {
        hasNavigationBar = resources.getBoolean(id);
      }
      try {
        @SuppressLint("PrivateApi") Class systemPropertiesClass =
            Class.forName("android.os.SystemProperties");
        Method m = systemPropertiesClass.getMethod("get", String.class);
        String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
        if ("1".equals(navBarOverride)) {
          hasNavigationBar = false;
        } else if ("0".equals(navBarOverride)) {
          hasNavigationBar = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return hasNavigationBar;
    }
  }

  private final static String TAG = "PlayApp";

//  public static void setRxException() {
//    RxJavaPlugins.setErrorHandler(e -> {
//      if (e instanceof UndeliverableException) {
//        e = e.getCause();
//        if (e != null) {
//          LogUtils.e(TAG, "UndeliverableException=" + e.getMessage());
//          SpiderMan.show(e);
//        }
//        return;
//      } else if ((e instanceof IOException)) {
//        // fine, irrelevant network problem or API that throws on cancellation
//        LogUtils.e(TAG, "IOException=" + e.getMessage());
//        return;
//      } else if (e instanceof InterruptedException) {
//        // fine, some blocking code was interrupted by a dispose call
//        LogUtils.e(TAG, "IOException=" + e.getMessage());
//        return;
//      } else if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
//        // that's likely a bug in the application
//        Objects.requireNonNull(Thread.currentThread().getUncaughtExceptionHandler())
//            .uncaughtException(Thread.currentThread(), e);
//        return;
//      } else if (e instanceof IllegalStateException) {
//        // that's a bug in RxJava or in a custom operator
//        Objects.requireNonNull(Thread.currentThread().getUncaughtExceptionHandler())
//            .uncaughtException(Thread.currentThread(), e);
//        return;
//      }
//      LogUtils.e(TAG, "unknown exception=" + e.getMessage());
//    });
//  }


  /**
   * 解决Error inflating class android.webkit.WebView 因难以避免WebView存在安全漏洞，系统遭受攻击，Android不允许特权进程应用使用WebView。如果使用了，便会抛出以上异常。
   */
  @SuppressLint("SoonBlockedPrivateApi") public static void hookWebView() {
    int sdkInt = Build.VERSION.SDK_INT;
    LogUtil.i("hookWebView", "sdkInt = " + sdkInt);
    try {
      @SuppressLint("PrivateApi") Class<?> factoryClass =
          Class.forName("android.webkit.WebViewFactory");
      @SuppressLint("DiscouragedPrivateApi") Field field =
          factoryClass.getDeclaredField("sProviderInstance");
      field.setAccessible(true);
      Object sProviderInstance = field.get(null);
      if (sProviderInstance != null) {
        LogUtil.i("hookWebView", "sProviderInstance isn't null");
        return;
      }

      Method getProviderClassMethod;
      if (sdkInt > 22) {
        getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
      } else if (sdkInt == 22) {
        getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
      } else {
        LogUtil.i("hookWebView", "Don't need to Hook WebView");
        return;
      }
      getProviderClassMethod.setAccessible(true);
      Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
      @SuppressLint("PrivateApi") Class<?> delegateClass =
          Class.forName("android.webkit.WebViewDelegate");
      Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
      delegateConstructor.setAccessible(true);
      if (sdkInt < 26) {//低于Android O版本
        if (factoryProviderClass != null) {
          Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
          if (providerConstructor != null) {
            providerConstructor.setAccessible(true);
            sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
          }
        }
      } else {
        Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
        chromiumMethodName.setAccessible(true);
        String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
        if (chromiumMethodNameStr == null) {
          chromiumMethodNameStr = "create";
        }
        if (factoryProviderClass != null) {
          Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
          if (staticFactory != null) {
            sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
          }
        }
      }

      if (sProviderInstance != null) {
        field.set("sProviderInstance", sProviderInstance);
        LogUtil.i("hookWebView", "Hook success!");
      } else {
        LogUtil.i("hookWebView", "Hook failed!");
      }
    } catch (Throwable e) {
      LogUtil.e("hookWebView", e.getMessage());
    }
  }


  public static void hideSoftInput(@NonNull final View view) {
    InputMethodManager imm =
            (InputMethodManager) Objects.requireNonNull(Play.INSTANCE.getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}