package com.yfy.core.util;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.yfy.core.util.gson.DoubleTypeAdapter;
import com.yfy.core.util.gson.IntegerTypeAdapter;
import com.yfy.core.util.gson.ListTypeAdapter;
import com.yfy.core.util.gson.LongTypeAdapter;
import com.yfy.core.util.gson.StringTypeAdapter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jx
 */
public class GsonUtils {
  private static final Gson sGson;

  static {
    sGson = new GsonBuilder().registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
        .registerTypeAdapter(int.class, new IntegerTypeAdapter())
        .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
        .registerTypeAdapter(double.class, new DoubleTypeAdapter())
        .registerTypeAdapter(Long.class, new LongTypeAdapter())
        .registerTypeAdapter(long.class, new LongTypeAdapter())
        .registerTypeAdapter(String.class, new StringTypeAdapter())
        .registerTypeAdapter(List.class, new ListTypeAdapter())
        .create();
  }

  public static Gson getGson() {
    return sGson;
  }

  @Nullable public static <T> T fromJson(String json, Class<T> clz) throws JsonSyntaxException {
    return sGson.fromJson(json, clz);
  }

  @Nullable public static <T> T fromJson(String json, Type type) throws JsonSyntaxException {
    return sGson.fromJson(json, type);
  }

  public static String toJson(Object object) throws JsonIOException {
    return sGson.toJson(object);
  }
}
