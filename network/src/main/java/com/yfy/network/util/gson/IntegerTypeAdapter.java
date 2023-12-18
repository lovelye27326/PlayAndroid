package com.yfy.network.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author jx on 2018/8/9.
 */
public class IntegerTypeAdapter extends TypeAdapter<Integer> {
  @Override public void write(JsonWriter out, Integer value) {
    try {
      if (value == null) {
        value = 0;
      }
      out.value(value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public Integer read(JsonReader in) {
    try {
      int value;
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
//        LogUtil.e("TypeAdapter", "null is not a number");
        return 0;
      }
      if (in.peek() == JsonToken.BOOLEAN) {
        boolean b = in.nextBoolean();
//        LogUtil.e("TypeAdapter", b + " is not a number");
        if (!b) {
          value = 0;
        } else {
          value = 1;
        }
        return value;
      }
      if (in.peek() == JsonToken.STRING) {
        String str = in.nextString();
        try {
          return Integer.parseInt(str);
        } catch (Exception e) {
//          LogUtil.e("TypeAdapter", str + " is not a number");
          return 0;
        }
      } else {
        value = in.nextInt();
        return value;
      }
    } catch (Exception e) {
      e.printStackTrace();
//      LogUtil.e("TypeAdapter", "Not a number, " + e.getMessage());
    }
    return 0;
  }
}