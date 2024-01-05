package com.yfy.network;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.security.MessageDigest;

/**
 * 圆角转换 日期： 2022年01月11日 14:10 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。 _              _           _     _   ____
 * _             _ _ / \   _ __ __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___ / _ \ | '_ \ /
 * _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \ / ___ \| | | | (_| | | | (_) | | (_| | ___)
 * | |_| |_| | (_| | | (_) | /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/
 * <p>
 * You never know what you can do until you try ! ----------------------------------------------------------------
 */
public class GlideCircleTransform extends BitmapTransformation {
  private final String TAG = getClass().getName();

  public GlideCircleTransform() {
    super();
  }

  @Override
  public Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth,
      int outHeight) {
    return circleCrop(pool, toTransform);
  }

  private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
    if (source == null) return null;
    int size = Math.min(source.getWidth(), source.getHeight());
    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;
    // TODO this could be acquired from the pool too
    Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
    //Bitmap result = pool.get(size, size, Bitmap.Config.RGB_565);
    Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
    if (result == null) {
      result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    }
    Canvas canvas = new Canvas(result);
    Paint paint = new Paint();
    //设置TileMode的样式 CLAMP 拉伸 REPEAT 重复  MIRROR 镜像
    paint.setShader(
        new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    paint.setAntiAlias(true);
    float r = size / 2f;
    canvas.drawCircle(r, r, r, paint);
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof GlideCircleTransform) {
      return this == obj;
    }
    return false;
  }

  @Override public int hashCode() {
    return Util.hashCode(TAG.hashCode());
  }

  @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    messageDigest.update(TAG.getBytes(CHARSET));
  }
}
