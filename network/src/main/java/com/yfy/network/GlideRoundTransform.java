package com.yfy.network;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.security.MessageDigest;

/**
 * 加载圆角转换器 日期： 2022年01月11日 14:18 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。 _              _           _     _
 * ____  _             _ _ / \   _ __ __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___ / _ \ |
 * '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \ / ___ \| | | | (_| | | | (_) | | (_|
 * |  ___) | |_| |_| | (_| | | (_) | /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/
 * \__|\__,_|\__,_|_|\___/
 * <p>
 * You never know what you can do until you try ! ----------------------------------------------------------------
 */
public class GlideRoundTransform extends CenterCrop {
  private static float radius = 0f;

  public GlideRoundTransform() {
    this(4);
  }

  public GlideRoundTransform(int dp) {
    super();
    radius = Resources.getSystem().getDisplayMetrics().density * dp;
  }

  @Override
  protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth,
      int outHeight) {
    Bitmap transform = super.transform(pool, toTransform, outWidth, outHeight);
    return roundCrop(pool, transform);
    //        return roundCrop(pool, toTransform);
  }

  private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
    if (source == null) return null;

    Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    if (result == null) {
      result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(result);
    Paint paint = new Paint();
    paint.setShader(
        new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    paint.setAntiAlias(true);
    RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
    canvas.drawRoundRect(rectF, radius, radius, paint);
    return result;
  }

  @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

  }
}
