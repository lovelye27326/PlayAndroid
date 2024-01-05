package com.yfy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yfy.core.util.ActivityUtil;
import com.yfy.core.util.ScreenUtils;
import com.yfy.network.CornerTransform;
import com.yfy.network.GlideApp;
import com.yfy.network.GlideCircleTransform;
import com.yfy.network.GlideRoundTransform;

import java.io.File;

/**
 * 图片加载封装类， 用GlideApp的加载api更佳
 */
public class GlideUtils {

  private GlideUtils() {
  }

  /**
   * 使用默认的占位图
   */
  public static void loadImage(Context context, ImageView view, File uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption =
        RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  public static void loadImageNoCache(Context context, ImageView imageView, File file) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true);
    Glide.with(context).load(file).apply(requestOption).into(imageView);
  }

  public static void loadImageNoCache(Context context, ImageView imageView, String uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true);
    Glide.with(context).load(uri).apply(requestOption).into(imageView);
  }

  public static void loadImage(Context context, ImageView view, String uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .diskCacheStrategy(
            DiskCacheStrategy.RESOURCE); //DiskCacheStrategy.RESOURCE-> Glide仅在磁盘上缓存所有转换（例如调整大小，裁剪）后的最终图像。当想要缓存完全处理的图像而不是原始数据时，此策略是理想的
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  public static void loadImage(Context context, ImageView view, int uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption =
        RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  public static void loadCircleImage(Context context, ImageView view, String uri, int resId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  /**
   * 加载图片
   *
   * @param context   ctx
   * @param url       url
   * @param imageView iv
   */
  public static void loadImg(Context context, String url, int resId, final ImageView imageView) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    if (resId != 0) {
      GlideApp.with(context).load(url).placeholder(resId).error(resId)
          //.centerCrop()
          .into(imageView);
    } else {
      GlideApp.with(context).load(url)
          //.centerCrop()
          .into(imageView);
    }
  }

  /**
   * 加载圆形图片
   *
   * @param context   ctx
   * @param url       url
   * @param imageView iv
   */
  public static void loadCircleImg(Context context, String url, int resId,
      final ImageView imageView) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    GlideApp.with(context).load(url).placeholder(resId).error(resId)
        .centerCrop()
        .transform(new GlideCircleTransform()).into(imageView);
  }

  /**
   * 加载图片
   *
   * @param url       url
   * @param imageView iv
   */
  public static void loadImgFrg(Fragment fragment, String url, int resId,
      final ImageView imageView) {
    if (checkContextIsInvalidate(fragment.getContext())) {
      return;
    }
    GlideApp.with(fragment).load(url).placeholder(resId).error(resId)
        //.centerCrop()
        .into(imageView);
  }

  public static void loadCircleImgFrg(Fragment fragment, String url, int resId,
      final ImageView imageView) {
    if (checkContextIsInvalidate(fragment.getContext())) {
      return;
    }
    GlideApp.with(fragment).load(url).placeholder(resId).error(resId)
        //.centerCrop()
        .transform(new GlideCircleTransform()).into(imageView);
  }

  /**
   * 加载圆角图片
   *
   * @param context ctx
   * @param uri     url
   * @param resId   err
   * @param view    iv
   */
  public static void loadRoundedCornersImage(Context context, String uri, int resId,
      ImageView view) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    GlideApp.with(context).load(uri).transform(new GlideRoundTransform(8)).error(resId).into(view);
  }

  public static void loadRoundedCornersImg(Context context, String uri, int corner, int resId,
      ImageView view) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    GlideApp.with(context)
        .load(uri)
        .transform(new GlideRoundTransform(corner))
        .error(resId)
        .into(view);
  }

  public static void loadRoundedCornersImgFrg(Fragment fragment, String uri, int corner, int resId,
      ImageView view) {
    if (checkContextIsInvalidate(fragment.getContext())) {
      return;
    }
    GlideApp.with(fragment)
        .load(uri)
        .transform(new GlideRoundTransform(corner))
        .error(resId)
        .into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, int uri, int resId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .transform(new CenterCrop(), new RoundedCorners(16))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, Uri uri,
      int roundRadius, @DrawableRes int placeholder) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .transform(new CenterInside(), new RoundedCorners(roundRadius))
        .placeholder(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, String uri,
      int resId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .transform(new CenterCrop(), new RoundedCorners(16))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, String uri,
      @DrawableRes int resId, int roundRadius) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .transform(new CenterCrop(), new RoundedCorners(roundRadius))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  /**
   * fragment加载图片
   */
  public static void loadRoundedCornersImage(Fragment context, ImageView view, String uri,
      @DrawableRes int resId, int roundRadius) {
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .transform(new CenterCrop(), new RoundedCorners(roundRadius))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  /**
   * fragment加载图片
   */
  public static void loadRoundedCornersImage(Fragment context, ImageView view, int uri,
      @DrawableRes int resId, int roundRadius) {
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .transform(new CenterCrop(), new RoundedCorners(roundRadius))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadImage(Fragment context, ImageView view, int uri) {
    RequestOptions requestOption =
        RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, String uri, int width,
      int height, int resId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .override(width, height)
        .transform(new FitCenter(), new CornerTransform(context, ScreenUtils.dp2px(context, 10f)))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, String uri, int width,
      int height, int resId, int roundRadius) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .override(width, height)
        .transform(new FitCenter(), new CornerTransform(context, roundRadius))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImg(Context context, String uri, int width, int height,
      int resId, int roundRadius, ImageView view) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    GlideApp.with(context)
        .load(uri)
        .override(width, height)
        .transform(new GlideRoundTransform(roundRadius))
        .placeholder(resId)
        .error(resId)
        .into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, String uri, int width,
      int height, int resId, int roundRadius, boolean... args) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    CornerTransform transformation = new CornerTransform(context, roundRadius);
    transformation.setExceptCorner(args[0], args[1], args[2], args[3]);
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .override(width, height)
        .transform(new FitCenter(), transformation)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadRoundedCornersImage(Context context, ImageView view, int uri, int width,
      int height, int resId, int roundRadius, boolean... args) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    CornerTransform transformation = new CornerTransform(context, roundRadius);
    transformation.setExceptCorner(args[0], args[1], args[2], args[3]);
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .override(width, height)
        .transform(new FitCenter(), transformation)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
  }

  public static void loadGif(Context context, ImageView view, String uri) {
    RequestOptions requestOption =
        RequestOptions.centerCropTransform().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).asGif().load(uri).apply(requestOption).into(view);
  }

  public static void loadGif(Context context, ImageView view, int uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption =
        RequestOptions.centerCropTransform().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).asGif().load(uri).apply(requestOption).into(view);
  }

  /**
   * 使用特定的占位图
   *
   * @param context ctx
   * @param view    v
   * @param uri     uri
   * @param resId   占位图片id
   */
  public static void loadImage(Context context, ImageView view, String uri, int resId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(resId)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(view);
  }

  /**
   * 加载Bitmap需要回调时使用
   */
  public static void loadImageTarget(CustomViewTarget<ImageView, Bitmap> simpleTarget, String uri,
      int placeholderId) {
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(placeholderId)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(ActivityUtil.getTopActivityOrApp()).asBitmap().load(uri).apply(requestOption).into(simpleTarget);
  }

  /**
   * 加载Bitmap需要回调时使用
   */
  public static void loadImageTarget(CustomViewTarget<ImageView, Bitmap> simpleTarget, String uri) {
    RequestOptions requestOption =
        RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(ActivityUtil.getTopActivityOrApp()).asBitmap().load(uri).apply(requestOption).into(simpleTarget);
  }

  /**
   * 加载图片时确定图片的大小
   */
  public static void loadImage(Context context, ImageView imageView, String uri, int width,
      int height, int placeholderId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.noAnimation()
        .placeholder(placeholderId)
        .override(width, height)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(imageView);
  }

  public static void loadImageWithoutCache(Context context, ImageView view, String uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);
    Glide.with(context).load(uri).apply(requestOptions).into(view);
  }

  public static void loadImageSource(Context context, ImageView imageView, String uri,
      int placeholderId) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    RequestOptions requestOption = RequestOptions.placeholderOf(placeholderId)
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    Glide.with(context).load(uri).apply(requestOption).into(imageView);
  }

  public static void downloadImageOnly(Context context, SimpleTarget<Bitmap> target, String uri) {
    if (checkContextIsInvalidate(context)) {
      return;
    }
    Glide.with(context).asBitmap().load(uri).into(target);
  }

  /**
   * 用于检查传入Context是否为空，或者Activity已经销毁
   */
  private static boolean checkContextIsInvalidate(Context context) {
    if (context == null) {
      return true;
    }
    return context instanceof Activity && ((Activity) context).isDestroyed();
  }

  //支持加载GIF图片
//  public static void loadRoundedCornersAllType(Context context, ImageView view, String uri,
//      int width, int height, int resId, int roundRadius) {
//    if (checkContextIsInvalidate(context)) {
//      return;
//    }
//    if (uri.endsWith(".gif") || uri.endsWith(".GIF")) {
//      RequestOptions requestOption = new RequestOptions().override(width, height)
//          .transform(new FitCenter(), new FilletTransformation(roundRadius))
//          .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//      Glide.with(context).asGif().load(uri).placeholder(resId).apply(requestOption).into(view);
//    } else {
//      RequestOptions requestOption = RequestOptions.noAnimation()
//          .override(width, height)
//          .transform(new FitCenter(), new CornerTransform(context, roundRadius))
//          .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//      Glide.with(context).load(uri).placeholder(resId).apply(requestOption).into(view);
//    }
//  }
}
