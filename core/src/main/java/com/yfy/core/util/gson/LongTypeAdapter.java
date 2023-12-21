package com.yfy.core.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author jx on 2018/8/9.
 */
public class LongTypeAdapter extends TypeAdapter<Long> {
  @Override public void write(JsonWriter out, Long value) {
    try {
      if (value == null) {
        value = 0L;
      }
      out.value(value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public Long read(JsonReader in) {
    try {
      long value;
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
//        LogUtil.e("TypeAdapter", "null is not a number");
        return 0L;
      }
      if (in.peek() == JsonToken.BOOLEAN) {
        boolean b = in.nextBoolean();
//        LogUtil.e("TypeAdapter", b + " is not a number");
        if (!b) {
          value = 0L;
        } else {
          value = 1L;
        }
        return value;
      }
      if (in.peek() == JsonToken.STRING) {
        String str = in.nextString();
        try {
          return Long.parseLong(str);
        } catch (Exception e) {
          return 0L;
        }
      } else {
        value = in.nextLong();
        return value;
      }
    } catch (Exception e) {
      e.printStackTrace();
//      LogUtil.e("TypeAdapter", "Not a number, " + e.getMessage());
    }
    return 0L;
  }
}
