package com.yfy.core.util;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.yfy.core.view.base.ActivityCollector;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2020/03/19
 *     desc  :
 *
 *  * 日期： 2023年12月20日 16:29
 *  * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *  * _              _           _     _   ____  _             _ _
 *  * / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *  * / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *  * / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  * /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  --
 *  * <p>
 *  * You never know what you can do until you try !
 *  * ----------------------------------------------------------------
 *
 * </pre>
 */
public class UtilsActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

    static final UtilsActivityLifecycleImpl INSTANCE = new UtilsActivityLifecycleImpl();

//    private final LinkedList<Activity> mActivityList = new LinkedList<>();

    private final List<Util.OnAppStatusChangedListener> mStatusListeners = new CopyOnWriteArrayList<>();
    private final Map<Activity, List<Util.ActivityLifecycleCallbacks>> mActivityLifecycleCallbacksMap = new ConcurrentHashMap<>();

//    private static final Activity STUB = new Activity(); //空的代理Activity

    private int mForegroundCount = 0;
    private int mConfigCount = 0;
    private boolean mIsBackground = false;

    void init(Application app) {
        LogUtil.i("UtilsActivityLifecycleImpl", "app init.");
        app.registerActivityLifecycleCallbacks(this);
    }

    void unInit(Application app) {
        ActivityCollector.INSTANCE.getActivityList().clear();
        app.unregisterActivityLifecycleCallbacks(this);
    }

    Activity getTopActivity() {
        if (ActivityCollector.INSTANCE.size() == 0) return null;
        for (final WeakReference<Activity> activityWeakReference : ActivityCollector.INSTANCE.getActivityList()) {
            if (activityWeakReference.get() != null) {
                Activity activity = activityWeakReference.get();
                if (!ScreenUtils.isActivityAlive(activity)) {
                    continue;
                }
                return activity;
            }
        }
        return null;
    }

    void addOnAppStatusChangedListener(final Util.OnAppStatusChangedListener listener) {
        mStatusListeners.add(listener);
    }

    void removeOnAppStatusChangedListener(final Util.OnAppStatusChangedListener listener) {
        mStatusListeners.remove(listener);
    }

//    void addActivityLifecycleCallbacks(final Util.ActivityLifecycleCallbacks listener) {
//        addActivityLifecycleCallbacks(STUB, listener);
//    }

    void addActivityLifecycleCallbacks(final Activity activity,
                                       final Util.ActivityLifecycleCallbacks listener) {
        if (activity == null || listener == null) return;
        ThreadUtils.runOnUiThread(() -> addActivityLifecycleCallbacksInner(activity, listener));
    }

    boolean isAppForeground() {
        return !mIsBackground;
    }

    private void addActivityLifecycleCallbacksInner(final Activity activity,
                                                    final Util.ActivityLifecycleCallbacks callbacks) {
        List<Util.ActivityLifecycleCallbacks> callbacksList = mActivityLifecycleCallbacksMap.get(activity);
        if (callbacksList == null) {
            callbacksList = new CopyOnWriteArrayList<>();
            mActivityLifecycleCallbacksMap.put(activity, callbacksList);
        } else {
            if (callbacksList.contains(callbacks)) return;
        }
        callbacksList.add(callbacks);
    }

//    void removeActivityLifecycleCallbacks(final Util.ActivityLifecycleCallbacks callbacks) {
//        removeActivityLifecycleCallbacks(STUB, callbacks);
//    }

    void removeActivityLifecycleCallbacks(final Activity activity) {
        if (activity == null) return;
        ThreadUtils.runOnUiThread(() -> mActivityLifecycleCallbacksMap.remove(activity));
    }

    void removeActivityLifecycleCallbacks(final Activity activity,
                                          final Util.ActivityLifecycleCallbacks callbacks) {
        if (activity == null || callbacks == null) return;
        ThreadUtils.runOnUiThread(() -> removeActivityLifecycleCallbacksInner(activity, callbacks));
    }

    private void removeActivityLifecycleCallbacksInner(final Activity activity,
                                                       final Util.ActivityLifecycleCallbacks callbacks) {
        List<Util.ActivityLifecycleCallbacks> callbacksList = mActivityLifecycleCallbacksMap.get(activity);
        if (callbacksList != null && !callbacksList.isEmpty()) {
            callbacksList.remove(callbacks);
        }
    }

    private void consumeActivityLifecycleCallbacks(Activity activity, Lifecycle.Event event) {
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap.get(activity));
//        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap.get(STUB));
    }

    private void consumeLifecycle(Activity activity, Lifecycle.Event
            event, List<Util.ActivityLifecycleCallbacks> listeners) {
        if (listeners == null) return;
        for (Util.ActivityLifecycleCallbacks listener : listeners) {
            listener.onLifecycleChanged(activity, event);
            if (event.equals(Lifecycle.Event.ON_CREATE)) {
                listener.onActivityCreated(activity);
            } else if (event.equals(Lifecycle.Event.ON_START)) {
                listener.onActivityStarted(activity);
            } else if (event.equals(Lifecycle.Event.ON_RESUME)) {
                listener.onActivityResumed(activity);
            } else if (event.equals(Lifecycle.Event.ON_PAUSE)) {
                listener.onActivityPaused(activity);
            } else if (event.equals(Lifecycle.Event.ON_STOP)) {
                listener.onActivityStopped(activity);
            } else if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                listener.onActivityDestroyed(activity);
            }
        }
        if (event.equals(Lifecycle.Event.ON_DESTROY)) {
            mActivityLifecycleCallbacksMap.remove(activity);
        }
    }

    Application getApplicationByReflect() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            if (thread == null) return null;
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) return null;
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // lifecycle start
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle
            savedInstanceState) {/**/}

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        if (ActivityCollector.INSTANCE.size() == 0) { //除当前页外，其他页Activity已结束的情况
            postStatus(activity, true);
        }
//        LanguageUtils.applyLanguage(activity);
        WeakReference<Activity> weakRefActivity = new WeakReference<>(activity);
        ActivityCollector.INSTANCE.add(weakRefActivity);
        setAnimatorsEnabled();
        setTopActivity(activity);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE);
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle
            savedInstanceState) {/**/}

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!mIsBackground) {
            setTopActivity(activity);
        }
        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START);
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityResumed(@NonNull final Activity activity) {
        setTopActivity(activity);
        if (mIsBackground) {
            mIsBackground = false;
            postStatus(activity, true);
        }
        processHideSoftInputOnActivityDestroy(activity, false);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                mIsBackground = true;
                postStatus(activity, false);
            }
        }
        processHideSoftInputOnActivityDestroy(activity, true);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_STOP);
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle
            outState) {/**/}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle
            outState) {/**/}

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle
            outState) {/**/}

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        boolean containActivity;
        WeakReference<Activity> weakRefActivity = null;
        LinkedList<WeakReference<Activity>> activityList = ActivityCollector.INSTANCE.getActivityList();
        for (final WeakReference<Activity> activityWeakReference : activityList) {
            if (activityWeakReference.get() != null) {
                Activity activityGet = activityWeakReference.get();
                containActivity = activity.equals(activityGet);
                if (containActivity) {
                    weakRefActivity = activityWeakReference;
                }
            }
        }
        if (weakRefActivity != null)
            ActivityCollector.INSTANCE.remove(weakRefActivity);
//        UtilsBridge.fixSoftInputLeaks(activity);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY);
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {/**/}
    ///////////////////////////////////////////////////////////////////////////
    // lifecycle end
    ///////////////////////////////////////////////////////////////////////////

    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
        try {
            if (isSave) {
                Window window = activity.getWindow();
                final WindowManager.LayoutParams attrs = window.getAttributes();
                final int softInputMode = attrs.softInputMode;
                window.getDecorView().setTag(-123, softInputMode);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                final Object tag = activity.getWindow().getDecorView().getTag(-123);
                if (!(tag instanceof Integer)) return;
                ThreadUtils.runOnUiThreadDelayed(() -> {
                    try {
                        Window window = activity.getWindow();
                        if (window != null) {
                            window.setSoftInputMode(((Integer) tag));
                        }
                    } catch (Exception ignore) {
                    }
                }, 100);
            }
        } catch (Exception ignore) {
        }
    }

    private void postStatus(final Activity activity, final boolean isForeground) {
        if (mStatusListeners.isEmpty()) return;
        for (Util.OnAppStatusChangedListener statusListener : mStatusListeners) {
            if (isForeground) {
                statusListener.onForeground(activity);
            } else {
                statusListener.onBackground(activity);
            }
        }
    }

    private void setTopActivity(final Activity activity) {
        LinkedList<WeakReference<Activity>> activityList = ActivityCollector.INSTANCE.getActivityList();
        if (activityList.size() <= 1) return;
        boolean containActivity = false;
        WeakReference<Activity> activityWeakRef = null;
        for (final WeakReference<Activity> activityWeakReference : activityList) {
            if (activityWeakReference.get() != null) {
                Activity activityGet = activityWeakReference.get();
                containActivity = activity.equals(activityGet);
                if (containActivity) {
                    activityWeakRef = activityWeakReference;
                }
            }
        }
        if (containActivity) {
            if (!activityList.getFirst().get().equals(activity)) {
                activityList.remove(activityWeakRef);
                WeakReference<Activity> weakRefActivity = new WeakReference<>(activity);
                activityList.addFirst(weakRefActivity);
            }
        } else {
            WeakReference<Activity> weakRefActivity = new WeakReference<>(activity);
            activityList.addFirst(weakRefActivity);
        }
    }

    public Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.getMessage());
            return null;
        }
    }

    private Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.getMessage());
            return null;
        }
    }

    /**
     * Set animators enabled.
     */
    private static void setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return;
        }
        try {
            //noinspection JavaReflectionMemberAccess
            @SuppressLint("SoonBlockedPrivateApi") Field sDurationScaleField = ValueAnimator.class.getDeclaredField("sDurationScale");
            sDurationScaleField.setAccessible(true);
            //noinspection ConstantConditions
            float sDurationScale = (Float) sDurationScaleField.get(null);
            if (sDurationScale == 0f) {
                sDurationScaleField.set(null, 1f);
                Log.i("UtilsActivityLifecycle", "setAnimatorsEnabled: Animators are enabled now!");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
