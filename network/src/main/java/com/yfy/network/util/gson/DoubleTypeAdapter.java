package com.yfy.network.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author jx on 2018/8/9.
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {
  @Override public void write(JsonWriter out, Double value) {
    try {
      if (value == null) {
        value = 0D;
      }
      out.value(value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public Double read(JsonReader in) {
    try {
      double value;
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        //        LogUtil.e("TypeAdapter", "null is not a number");
        return 0D;
      }
      if (in.peek() == JsonToken.BOOLEAN) {
        boolean b = in.nextBoolean();
        //        LogUtil.e("TypeAdapter", b + " is not a number");
        if (!b) {
          value = 0d;
        } else {
          value = 1d;
        }
        return value;
      }
      if (in.peek() == JsonToken.STRING) {
        String str = in.nextString();
        try {
          return Double.parseDouble(str);
        } catch (Exception e) {
          //          LogUtil.e("TypeAdapter", str + " is not a number");
          return 0D;
        }
      } else {
        value = in.nextDouble();
        return value;
      }
    } catch (Exception e) {
      e.printStackTrace();
      //      LogUtil.e("TypeAdapter", "err: " + e.getMessage());
    }
    return 0D;
  }
}