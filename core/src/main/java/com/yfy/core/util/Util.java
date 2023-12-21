package com.yfy.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.yfy.core.Play;

/**
 * 日期： 2023年12月20日 16:24
 * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 * _              _           _     _   ____  _             _ _
 * / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 * / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 * / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 * /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  --
 * <p>
 * You never know what you can do until you try !
 * ----------------------------------------------------------------
 */
public class Util {
//    @SuppressLint("StaticFieldLeak")
//    private static Application sApp;

    private Util() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static void init(final Application app) {
        if (app == null) {
            LogUtil.e("Utils", "app is null.");
            return;
        }
        Application sApp = (Application) Play.INSTANCE.getContext();
        if (sApp == null) {
            sApp = app;
            Play.INSTANCE.initialize(sApp);
            UtilsActivityLifecycleImpl.INSTANCE.init(sApp);
//            preLoad();
            return;
        }
        if (sApp.equals(app)) {
            UtilsActivityLifecycleImpl.INSTANCE.init(sApp);
            return;
        }
        UtilsActivityLifecycleImpl.INSTANCE.unInit(sApp); //新旧application对象不同，卸载再重新初始化
        sApp = app;
        UtilsActivityLifecycleImpl.INSTANCE.init(sApp);
    }


//    static void preLoad() {
//        preLoad(AdaptScreenUtils.getPreLoadRunnable());
//    }

//    private static void preLoad(final Runnable... runs) {
//        for (final Runnable r : runs) {
//            ThreadUtils.getCachedPool().execute(r);
//        }
//    }


    static boolean isAppForeground() {
        return UtilsActivityLifecycleImpl.INSTANCE.isAppForeground();
    }


    static void addOnAppStatusChangedListener(final Util.OnAppStatusChangedListener listener) {
        UtilsActivityLifecycleImpl.INSTANCE.addOnAppStatusChangedListener(listener);
    }

    static void removeOnAppStatusChangedListener(final Util.OnAppStatusChangedListener listener) {
        UtilsActivityLifecycleImpl.INSTANCE.removeOnAppStatusChangedListener(listener);
    }

//    static void addActivityLifecycleCallbacks(final Util.ActivityLifecycleCallbacks callbacks) {
//        UtilsActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(callbacks);
//    }

//    static void removeActivityLifecycleCallbacks(final Util.ActivityLifecycleCallbacks callbacks) {
//        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(callbacks);
//    }


    public static Application getApp() {
        Context sApp = Play.INSTANCE.getContext();
        if (sApp != null) return (Application) sApp;
        init(UtilsActivityLifecycleImpl.INSTANCE.getApplicationByReflect());
        if (sApp == null) throw new NullPointerException("reflect failed.");
        LogUtil.i("Util", sApp.getPackageName() + " ,reflect app success.");
        return (Application) sApp;
    }

    static void addActivityLifecycleCallbacks(final Activity activity,
                                              final Util.ActivityLifecycleCallbacks callbacks) {
        UtilsActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(activity, callbacks);
    }

    static void removeActivityLifecycleCallbacks(final Activity activity) {
        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity);
    }

    static void removeActivityLifecycleCallbacks(final Activity activity,
                                                 final Util.ActivityLifecycleCallbacks callbacks) {
        UtilsActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity, callbacks);
    }

    public interface OnAppStatusChangedListener {
        void onForeground(Activity activity);

        void onBackground(Activity activity);
    }

    public static class ActivityLifecycleCallbacks {

        public void onActivityCreated(@NonNull Activity activity) {/**/}

        public void onActivityStarted(@NonNull Activity activity) {/**/}

        public void onActivityResumed(@NonNull Activity activity) {/**/}

        public void onActivityPaused(@NonNull Activity activity) {/**/}

        public void onActivityStopped(@NonNull Activity activity) {/**/}

        public void onActivityDestroyed(@NonNull Activity activity) {/**/}

        public void onLifecycleChanged(@NonNull Activity activity, Lifecycle.Event event) {/**/}
    }
}
