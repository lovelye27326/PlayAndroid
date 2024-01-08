package com.yfy.core.util;

import static com.yfy.core.util.BarUtilsKt.getNavBarHeight;
import static com.yfy.core.util.BarUtilsKt.getStatusBarHeight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.yfy.core.Play;
import com.yfy.core.R;
import com.yfy.core.view.base.ActivityCollector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * <pre>
 *     author: Blankj/Yfy
 *     blog  : http://blankj.com
 *     time  : 2016/09/29
 *     desc  : utils about toast
 *
 *
 *
 *  * 提示
 *  * 日期： 2023年12月20日 14:00
 *  * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *  * _              _           _     _   ____  _             _ _
 *  * / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *  * / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *  * / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  * /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  -- yfy
 *  * <p>
 *  * You never know what you can do until you try !
 *  * ----------------------------------------------------------------
 * </pre>
 */
public class ToastUtil {

    @StringDef({MODE.LIGHT, MODE.DARK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MODE {
        String LIGHT = "light";
        String DARK = "dark";
    }

    private static final String TAG_TOAST = "TAG_TOAST";
    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final String NULL = "toast null";
    private static final String NOTHING = "toast nothing";
    private static final ToastUtil DEFAULT_MAKER = make();

    private static WeakReference<IToast> sWeakToast;

    private String mMode;
    private int mGravity = -1;
    private int mXOffset = -1;
    private int mYOffset = -1;
    private int mBgColor = COLOR_DEFAULT;
    private int mBgResource = -1;
    private int mTextColor = COLOR_DEFAULT;
    private int mTextSize = -1;
    private boolean isLong = false;
    private Drawable[] mIcons = new Drawable[4];
    private boolean isNotUseSystemToast = false;

    /**
     * Make a toast.
     *
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public static ToastUtil make() {
        return new ToastUtil();
    }

    /**
     * @param mode The mode.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setMode(@MODE String mode) {
        mMode = mode;
        return this;
    }

    /**
     * Set the gravity.
     *
     * @param gravity The gravity.
     * @param xOffset X-axis offset, in pixel.
     * @param yOffset Y-axis offset, in pixel.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setGravity(final int gravity, final int xOffset, final int yOffset) {
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
        return this;
    }

    /**
     * Set the color of background.
     *
     * @param backgroundColor The color of background.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setBgColor(@ColorInt final int backgroundColor) {
        mBgColor = backgroundColor;
        return this;
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setBgResource(@DrawableRes final int bgResource) {
        mBgResource = bgResource;
        return this;
    }

    /**
     * Set the text color of toast.
     *
     * @param msgColor The text color of toast.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setTextColor(@ColorInt final int msgColor) {
        mTextColor = msgColor;
        return this;
    }

    /**
     * Set the text size of toast.
     *
     * @param textSize The text size of toast.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setTextSize(final int textSize) {
        mTextSize = textSize;
        return this;
    }

    /**
     * Set the toast for a long period of time.
     *
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setDurationIsLong(boolean isLong) {
        this.isLong = isLong;
        return this;
    }

    /**
     * Set the left icon of toast.
     *
     * @param resId The left icon resource identifier.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setLeftIcon(@DrawableRes int resId) {
        return setLeftIcon(ContextCompat.getDrawable(Objects.requireNonNull(Play.INSTANCE.getContext()), resId));
    }

    /**
     * Set the left icon of toast.
     *
     * @param drawable The left icon drawable.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setLeftIcon(@Nullable Drawable drawable) {
        mIcons[0] = drawable;
        return this;
    }

    /**
     * Set the top icon of toast.
     *
     * @param resId The top icon resource identifier.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setTopIcon(@DrawableRes int resId) {
        return setTopIcon(ContextCompat.getDrawable(Objects.requireNonNull(Play.INSTANCE.getContext()), resId));
    }

    /**
     * Set the top icon of toast.
     *
     * @param drawable The top icon drawable.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setTopIcon(@Nullable Drawable drawable) {
        mIcons[1] = drawable;
        return this;
    }

    /**
     * Set the right icon of toast.
     *
     * @param resId The right icon resource identifier.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setRightIcon(@DrawableRes int resId) {
        return setRightIcon(ContextCompat.getDrawable(Objects.requireNonNull(Play.INSTANCE.getContext()), resId));
    }

    /**
     * Set the right icon of toast.
     *
     * @param drawable The right icon drawable.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setRightIcon(@Nullable Drawable drawable) {
        mIcons[2] = drawable;
        return this;
    }

    /**
     * Set the left bottom of toast.
     *
     * @param resId The bottom icon resource identifier.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setBottomIcon(int resId) {
        return setBottomIcon(ContextCompat.getDrawable(Objects.requireNonNull(Play.INSTANCE.getContext()), resId));
    }

    /**
     * Set the bottom icon of toast.
     *
     * @param drawable The bottom icon drawable.
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setBottomIcon(@Nullable Drawable drawable) {
        mIcons[3] = drawable;
        return this;
    }

    /**
     * Set not use system toast.
     *
     * @return the single {@link ToastUtil} instance
     */
    @NonNull
    public final ToastUtil setNotUseSystemToast() {
        isNotUseSystemToast = true;
        return this;
    }

    /**
     * Return the default {@link ToastUtil} instance.
     *
     * @return the default {@link ToastUtil} instance
     */
    @NonNull
    public static ToastUtil getDefaultMaker() {
        return DEFAULT_MAKER;
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param text The text.
     */
    public final void show(@Nullable final CharSequence text) {
        show(text, getDuration(), this);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     */
    public final void show(@StringRes final int resId) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId), getDuration(), this);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    public final void show(@StringRes final int resId, final Object... args) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId, args), getDuration(), this);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    public final void show(@Nullable final String format, final Object... args) {
        show(StringUtil.format(format, args), getDuration(), this);
    }

    /**
     * Show custom toast.
     */
    public final void show(@NonNull final View view) {
        show(view, getDuration(), this);
    }

    private int getDuration() {
        return isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
    }

    private View tryApplyUtilsToastView(final CharSequence text) {
        if (!MODE.DARK.equals(mMode) && !MODE.LIGHT.equals(mMode)
                && mIcons[0] == null && mIcons[1] == null && mIcons[2] == null && mIcons[3] == null) {
            return null;
        }

        View toastView = ScreenUtils.layoutId2View(R.layout.utils_toast_view);
        TextView messageTv = toastView.findViewById(android.R.id.message);
        if (MODE.DARK.equals(mMode)) {
            GradientDrawable bg = (GradientDrawable) toastView.getBackground().mutate();
            bg.setColor(Color.parseColor("#BB000000"));
            messageTv.setTextColor(Color.WHITE);
        }
        messageTv.setText(text);
        if (mIcons[0] != null) {
            View leftIconView = toastView.findViewById(R.id.utvLeftIconView);
            ViewCompat.setBackground(leftIconView, mIcons[0]);
            leftIconView.setVisibility(View.VISIBLE);
        }
        if (mIcons[1] != null) {
            View topIconView = toastView.findViewById(R.id.utvTopIconView);
            ViewCompat.setBackground(topIconView, mIcons[1]);
            topIconView.setVisibility(View.VISIBLE);
        }
        if (mIcons[2] != null) {
            View rightIconView = toastView.findViewById(R.id.utvRightIconView);
            ViewCompat.setBackground(rightIconView, mIcons[2]);
            rightIconView.setVisibility(View.VISIBLE);
        }
        if (mIcons[3] != null) {
            View bottomIconView = toastView.findViewById(R.id.utvBottomIconView);
            ViewCompat.setBackground(bottomIconView, mIcons[3]);
            bottomIconView.setVisibility(View.VISIBLE);
        }
        return toastView;
    }


    /**
     * Show the toast for a short period of time.
     *
     * @param text The text.
     */
    public static void showShort(@Nullable final CharSequence text) {
        show(text, Toast.LENGTH_SHORT, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     */
    public static void showShort(@StringRes final int resId) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId), Toast.LENGTH_SHORT, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    public static void showShort(@StringRes final int resId, final Object... args) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId, args), Toast.LENGTH_SHORT, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    public static void showShort(@Nullable final String format, final Object... args) {
        show(StringUtil.format(format, args), Toast.LENGTH_SHORT, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param text The text.
     */
    public static void showLong(@Nullable final CharSequence text) {
        show(text, Toast.LENGTH_LONG, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     */
    public static void showLong(@StringRes final int resId) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId), Toast.LENGTH_LONG, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    public static void showLong(@StringRes final int resId, final Object... args) {
        show(Objects.requireNonNull(Play.INSTANCE.getContext()).getString(resId, args), Toast.LENGTH_LONG, DEFAULT_MAKER);
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    public static void showLong(@Nullable final String format, final Object... args) {
        show(StringUtil.format(format, args), Toast.LENGTH_LONG, DEFAULT_MAKER);
    }

    /**
     * Cancel the toast.
     */
    public static void cancel() {
        ThreadUtils.runOnUiThread(() -> {
            if (sWeakToast != null) {
                final IToast iToast = ToastUtil.sWeakToast.get();
                if (iToast != null) {
                    iToast.cancel();
                }
                sWeakToast = null;
            }
        });
    }

    private static void show(@Nullable final CharSequence text, final int duration, final ToastUtil utils) {
        show(null, getToastFriendlyText(text), duration, utils);
    }

    private static void show(@NonNull final View view, final int duration, final ToastUtil utils) {
        show(view, null, duration, utils);
    }

    private static void show(@Nullable final View view,
                             @Nullable final CharSequence text,
                             final int duration,
                             @NonNull final ToastUtil utils) {
        ThreadUtils.runOnUiThread(() -> {
            cancel();
            IToast iToast = newToast(utils);
            ToastUtil.sWeakToast = new WeakReference<>(iToast);
            if (view != null) {
                iToast.setToastView(view);
            } else {
                iToast.setToastView(text);
            }
            iToast.show(duration);
        });
    }

    private static CharSequence getToastFriendlyText(CharSequence src) {
        CharSequence text = src;
        if (text == null) {
            text = NULL;
        } else if (text.length() == 0) {
            text = NOTHING;
        }
        return text;
    }

    private static IToast newToast(ToastUtil toastUtils) {
        if (!toastUtils.isNotUseSystemToast) {
            if (NotificationManagerCompat.from(Objects.requireNonNull(Play.INSTANCE.getContext())).areNotificationsEnabled()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return new SystemToast(toastUtils);
                }
                if (!ScreenUtils.isGrantedDrawOverlays()) {
                    return new SystemToast(toastUtils);
                }
            }
        }

        // not use system or notification disable
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return new WindowManagerToast(toastUtils, WindowManager.LayoutParams.TYPE_TOAST);
        } else if (ScreenUtils.isGrantedDrawOverlays()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new WindowManagerToast(toastUtils, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                return new WindowManagerToast(toastUtils, WindowManager.LayoutParams.TYPE_PHONE);
            }
        }
        return new ActivityToast(toastUtils);
    }

    static final class SystemToast extends AbsToast {

        SystemToast(ToastUtil toastUtils) {
            super(toastUtils);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    //noinspection JavaReflectionMemberAccess
                    @SuppressLint("DiscouragedPrivateApi") Field mTNField = Toast.class.getDeclaredField("mTN");
                    mTNField.setAccessible(true);
                    Object mTN = mTNField.get(mToast);
                    Field mTNmHandlerField = mTNField.getType().getDeclaredField("mHandler");
                    mTNmHandlerField.setAccessible(true);
                    Handler tnHandler = (Handler) mTNmHandlerField.get(mTN);
                    mTNmHandlerField.set(mTN, new SafeHandler(tnHandler));
                } catch (Exception ignored) {/**/}
            }
        }

        @Override
        public void show(int duration) {
            if (mToast == null) return;
            mToast.setDuration(duration);
            mToast.show();
        }

        static class SafeHandler extends Handler {
            private Handler impl;

            SafeHandler(Handler impl) {
                this.impl = impl;
            }

            @Override
            public void handleMessage(@NonNull Message msg) {
                impl.handleMessage(msg);
            }

            @Override
            public void dispatchMessage(@NonNull Message msg) {
                try {
                    impl.dispatchMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static final class WindowManagerToast extends AbsToast {

        private WindowManager mWM;

        private final WindowManager.LayoutParams mParams;

        WindowManagerToast(ToastUtil toastUtils, int type) {
            super(toastUtils);
            mParams = new WindowManager.LayoutParams();
            mWM = (WindowManager) Objects.requireNonNull(Play.INSTANCE.getContext()).getSystemService(Context.WINDOW_SERVICE);
            mParams.type = type;
        }

        WindowManagerToast(ToastUtil toastUtils, WindowManager wm, int type) {
            super(toastUtils);
            mParams = new WindowManager.LayoutParams();
            mWM = wm;
            mParams.type = type;
        }

        @Override
        public void show(final int duration) {
            if (mToast == null) return;
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.windowAnimations = android.R.style.Animation_Toast;
            mParams.setTitle("ToastWithoutNotification");
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.packageName = Objects.requireNonNull(Play.INSTANCE.getContext()).getPackageName();

            mParams.gravity = mToast.getGravity();
            if ((mParams.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((mParams.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }

            mParams.x = mToast.getXOffset();
            mParams.y = mToast.getYOffset();
            mParams.horizontalMargin = mToast.getHorizontalMargin();
            mParams.verticalMargin = mToast.getVerticalMargin();

            try {
                if (mWM != null) {
                    mWM.addView(mToastView, mParams);
                }
            } catch (Exception ignored) {/**/}

            ThreadUtils.runOnUiThreadDelayed(this::cancel, duration == Toast.LENGTH_SHORT ? 2000 : 3500);
        }

        @Override
        public void cancel() {
            try {
                if (mWM != null) {
                    mWM.removeViewImmediate(mToastView);
                    mWM = null;
                }
            } catch (Exception ignored) {/**/}
            super.cancel();
        }
    }

    static final class ActivityToast extends AbsToast {

        private static int sShowingIndex = 0;

        private Util.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
        private IToast iToast;

        ActivityToast(ToastUtil toastUtils) {
            super(toastUtils);
        }

        @Override
        public void show(int duration) {
            if (mToast == null) return;
            if (!Util.isAppForeground()) {
                // try to use system toast 用showSystemToast显示系统toast,再返回显示的吐司对象
                iToast = showSystemToast(duration);
                return;
            }
            boolean hasAliveActivity = false;
            Activity activity = null;
            for (final WeakReference<Activity> activityWeakReference : ActivityCollector.INSTANCE.getActivityList()) {
                if (activityWeakReference.get() != null) {
                    activity = activityWeakReference.get();
                    if (ScreenUtils.isActivityAlive(activity)) {
                        continue;
                    }
                    if (!hasAliveActivity) {
                        hasAliveActivity = true;
                        iToast = showWithActivityWindow(activity, duration);
                    } else { //Activity不在前台
                        showWithActivityView(activity, sShowingIndex, true);
                    }
                }
            }
            if (hasAliveActivity) {
                registerLifecycleCallback(activity);
                ThreadUtils.runOnUiThreadDelayed(this::cancel, duration == Toast.LENGTH_SHORT ? 2000 : 3500);
                ++sShowingIndex;
            } else {
                // try to use system toast
                iToast = showSystemToast(duration);
            }
        }

        @Override
        public void cancel() {
            if (isShowing()) {
                if (ActivityCollector.INSTANCE.size() == 0) return;
                for (final WeakReference<Activity> activityWeakReference : ActivityCollector.INSTANCE.getActivityList()) {
                    if (activityWeakReference.get() != null) {
                        Activity activity = activityWeakReference.get();
                        if (ScreenUtils.isActivityAlive(activity)) {
                            continue;
                        }
                        final Window window = activity.getWindow();
                        if (window != null) {
                            ViewGroup decorView = (ViewGroup) window.getDecorView();
                            View toastView = decorView.findViewWithTag(TAG_TOAST + (sShowingIndex - 1));
                            if (toastView != null) {
                                try {
                                    decorView.removeView(toastView);
                                } catch (Exception ignored) {/**/}
                            }
                        }
                        unregisterLifecycleCallback(activity);
                    }
                }
            }
            if (iToast != null) {
                iToast.cancel();
                iToast = null;
            }
            super.cancel();
        }

        private IToast showSystemToast(int duration) {
            SystemToast systemToast = new SystemToast(mToastUtils);
            systemToast.mToast = mToast;
            systemToast.show(duration);
            return systemToast;
        }

        private IToast showWithActivityWindow(Activity activity, int duration) {
            WindowManagerToast wmToast = new WindowManagerToast(mToastUtils, activity.getWindowManager(), WindowManager.LayoutParams.LAST_APPLICATION_WINDOW);
            wmToast.mToastView = getToastViewSnapshot(-1);
            wmToast.mToast = mToast;
            wmToast.show(duration);
            return wmToast;
        }

        private void showWithActivityView(final Activity activity, final int index, boolean useAnim) {
            final Window window = activity.getWindow();
            if (window != null) {
                final ViewGroup decorView = (ViewGroup) window.getDecorView();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp.gravity = mToast.getGravity();
                lp.bottomMargin = mToast.getYOffset() + getNavBarHeight();
                lp.topMargin = mToast.getYOffset() + getStatusBarHeight();
                lp.leftMargin = mToast.getXOffset();
                View toastViewSnapshot = getToastViewSnapshot(index);
                if (useAnim) {
                    toastViewSnapshot.setAlpha(0);
                    toastViewSnapshot.animate().alpha(1).setDuration(200).start();
                }
                decorView.addView(toastViewSnapshot, lp);
            }
        }

        private void registerLifecycleCallback(Activity activity) {
            final int index = sShowingIndex;
            mActivityLifecycleCallbacks = new Util.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity) {
                    if (isShowing()) {
                        showWithActivityView(activity, index, false);
                    }
                }
            };
            Util.addActivityLifecycleCallbacks(activity, mActivityLifecycleCallbacks);
        }

        private void unregisterLifecycleCallback(Activity activity) {
            Util.removeActivityLifecycleCallbacks(activity, mActivityLifecycleCallbacks);
            mActivityLifecycleCallbacks = null;
        }

        private boolean isShowing() {
            return mActivityLifecycleCallbacks != null;
        }
    }

    static abstract class AbsToast implements IToast {

        protected Toast mToast;
        protected ToastUtil mToastUtils;
        protected View mToastView;

        AbsToast(ToastUtil toastUtils) {
            mToast = new Toast(Play.INSTANCE.getContext());
            mToastUtils = toastUtils;

            if (mToastUtils.mGravity != -1 || mToastUtils.mXOffset != -1 || mToastUtils.mYOffset != -1) {
                mToast.setGravity(mToastUtils.mGravity, mToastUtils.mXOffset, mToastUtils.mYOffset);
            }
        }

        @Override
        public void setToastView(View view) {
            mToastView = view;
            mToast.setView(mToastView);
        }

        @Override
        public void setToastView(CharSequence text) {
            View utilsToastView = mToastUtils.tryApplyUtilsToastView(text);
            if (utilsToastView != null) {
                setToastView(utilsToastView);
                processRtlIfNeed();
                return;
            }

            mToastView = mToast.getView();
            if (mToastView == null || mToastView.findViewById(android.R.id.message) == null) {
                setToastView(ScreenUtils.layoutId2View(R.layout.utils_toast_view));
            }

            TextView messageTv = mToastView.findViewById(android.R.id.message);
            messageTv.setText(text);
            if (mToastUtils.mTextColor != COLOR_DEFAULT) {
                messageTv.setTextColor(mToastUtils.mTextColor);
            }
            if (mToastUtils.mTextSize != -1) {
                messageTv.setTextSize(mToastUtils.mTextSize);
            }
            setBg(messageTv);
            processRtlIfNeed();
        }

        private void processRtlIfNeed() {
            if (ScreenUtils.isLayoutRtl()) {
                setToastView(getToastViewSnapshot(-1));
            }
        }

        private void setBg(final TextView msgTv) {
            if (mToastUtils.mBgResource != -1) {
                mToastView.setBackgroundResource(mToastUtils.mBgResource);
                msgTv.setBackgroundColor(Color.TRANSPARENT);
            } else if (mToastUtils.mBgColor != COLOR_DEFAULT) {
                Drawable toastBg = mToastView.getBackground();
                Drawable msgBg = msgTv.getBackground();
                if (toastBg != null && msgBg != null) {
                    toastBg.mutate().setColorFilter(new PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN));
                    msgTv.setBackgroundColor(Color.TRANSPARENT);
                } else if (toastBg != null) {
                    toastBg.mutate().setColorFilter(new PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN));
                } else if (msgBg != null) {
                    msgBg.mutate().setColorFilter(new PorterDuffColorFilter(mToastUtils.mBgColor, PorterDuff.Mode.SRC_IN));
                } else {
                    mToastView.setBackgroundColor(mToastUtils.mBgColor);
                }
            }
        }

        @Override
        @CallSuper
        public void cancel() {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = null;
            mToastView = null;
        }

        View getToastViewSnapshot(final int index) {
            Bitmap bitmap = ImageUtils.view2Bitmap(mToastView);
            ImageView toastIv = new ImageView(Play.INSTANCE.getContext());
            toastIv.setTag(TAG_TOAST + index);
            toastIv.setImageBitmap(bitmap);
            return toastIv;
        }
    }

    interface IToast {

        void setToastView(View view);

        void setToastView(CharSequence text);

        void show(int duration);

        void cancel();
    }
}