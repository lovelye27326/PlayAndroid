package com.yfy.core.util.gson;

import com.google.gson.Gson;

/**
 * gson单例
 */
public class GsonUtil {
  private GsonUtil() {
  }

  public static Gson getInstance() {
    return GsonSingleHolder.gson;
  }

  private static class GsonSingleHolder {
    private static final Gson gson = new Gson();
  }
}
