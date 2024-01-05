package com.yfy.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.yfy.core.util.LogUtil;
import com.yfy.network.base.ServiceCreator;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 *
 * 配置
 * You never know what you can do until you try ! ----------------------------------------------------------------
 */
@Excludes(value = OkHttpLibraryGlideModule.class) @GlideModule public class MyGlideModule
    extends AppGlideModule {

  @Override public void registerComponents(@NonNull Context context, @NonNull Glide glide,
      @NonNull Registry registry) {
    OkHttpClient okHttpClient = ServiceCreator.INSTANCE.getOkHttpClient();
    if (okHttpClient == null) {
      okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true)
          .connectTimeout(30, TimeUnit.SECONDS)
          .readTimeout(30, TimeUnit.SECONDS)
          .readTimeout(30, TimeUnit.SECONDS)
          .build();
    }
    registry.replace(GlideUrl.class, InputStream.class,
        new OkHttpUrlLoader.Factory((Call.Factory) okHttpClient));
  }

  @Override public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    File dir = context.getExternalFilesDir(null);
    if (dir != null && dir.exists()) {
      String diskCacheFolder = dir.getAbsolutePath();
      LogUtil.i("MyGlideModel", "diskCacheFolder->" + diskCacheFolder);
      builder.setDiskCache(
          new DiskLruCacheFactory(diskCacheFolder, "imageCache", 1024 * 1024 * 50));
    } else {
      LogUtil.e("MyGlideModel", "diskCacheFolder-> null");
      String root = context.getFilesDir().getAbsolutePath();
      builder.setDiskCache(new DiskLruCacheFactory(root, "imageCache", 1024 * 1024 * 50));
    }
    builder.setDefaultRequestOptions(
        new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .format(DecodeFormat.PREFER_RGB_565)); //设置rgb565
  }

  @Override public boolean isManifestParsingEnabled() {
    return false;
  }
}
