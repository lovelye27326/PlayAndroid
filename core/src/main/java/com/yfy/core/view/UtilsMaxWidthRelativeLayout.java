package com.yfy.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yfy.core.Play;
import com.yfy.core.util.ScreenUtils;

import java.util.Objects;

/**
 * 日期： 2023年12月20日 15:40
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
public class UtilsMaxWidthRelativeLayout extends RelativeLayout {

    private static final int SPACING = ScreenUtils.dp2px(Play.INSTANCE.getContext(), 80f);

    public UtilsMaxWidthRelativeLayout(Context context) {
        super(context);
    }

    public UtilsMaxWidthRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UtilsMaxWidthRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMaxSpec = MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(Objects.requireNonNull(Play.INSTANCE.getContext())) - SPACING, MeasureSpec.AT_MOST);
        super.onMeasure(widthMaxSpec, heightMeasureSpec);
    }
}