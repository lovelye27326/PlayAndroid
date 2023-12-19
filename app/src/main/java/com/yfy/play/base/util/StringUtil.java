package com.yfy.play.base.util;

import static java.util.logging.Level.WARNING;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;

/**
 * 字符串相关工具类.
 *
 * @author bin.teng
 */
public class StringUtil {
  public static final int INDEX_NOT_FOUND = -1;

  /**
   * 检查是否为空字符串, null字符串也算空字符串.
   *
   * @param str 手机号
   * @return 空字符串和null字符串返回true, 否则返回false
   */
  public static boolean isBlank(String str) {
    int strLen = 0;
    if (str == null || (strLen = str.length()) == 0) {
      return true;
    }
    if ("null".equalsIgnoreCase(str)) return true;
    for (int i = 0; i < strLen; i++) {
      if ((Character.isWhitespace(str.charAt(i)) == false)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 空转默认支付
   *
   * @param str        s
   * @param defaultStr d
   * @return s
   */
  public static String empty2Default(String str, String defaultStr) {
    if (isEmpty(str)) {
      if (isEmpty(defaultStr)) {
        return "";
      }
      return defaultStr;
    }
    return str;
  }

  public static boolean isEmpty(String str) {
    return isBlank(str);
  }

  public static boolean isNotEmpty(String str) {
    return !StringUtil.isEmpty(str);
  }

  public static boolean isNotBlank(String str) {
    return !StringUtil.isBlank(str);
  }

  public static boolean isNotBlank(Integer str) {
    return !StringUtil.isBlank(str);
  }

  public static boolean isNotBlank(Long str) {
    return !StringUtil.isBlank(str);
  }

  public static boolean isBlank(Integer str) {
    return str == null;
  }

  public static boolean isBlank(Long str) {
    return str == null;
  }

  /**
   * 检查是否是合法手机号.
   *
   * @param str 手机号
   * @return 合法返回true, 否则返回false
   */
  public static boolean checkMobile(String str) {
    Pattern p = Pattern.compile("1[3456789][0-9]{9}");
    Matcher m = p.matcher(str);
    return m.matches();
  }

  /**
   * 检查是否是合法手机号加区号.
   *
   * @param str 手机号
   * @return 合法返回true, 否则返回false
   */
  public static boolean checkMobileWithCountry(String str) {
    Pattern p = Pattern.compile("(\\+86)?1[34578][0-9]{9}");
    Matcher m = p.matcher(str);
    return m.matches();
  }

  /**
   * 验证昵称的合法性(昵称不能为特殊字符)
   *
   * @param str s
   * @return b
   */
  public static boolean checkNickName(String str) {
    Pattern p = Pattern.compile("^[\\w+$\u4e00-\u9fa5]+$");
    Matcher m = p.matcher(str);
    return m.matches();
  }

  /**
   * 验证password的合法性(必须是6-14数字或字母及组合)
   *
   * @param str s
   * @return b
   */
  public static boolean checkPassword(String str) {
    Pattern p = Pattern.compile("^[\\w+$]{6,14}+$");
    Matcher m = p.matcher(str);
    return m.matches();
  }

  public static String stripEnd(String str, String stripChars) {
    int end;
    if (str == null || (end = str.length()) == 0) {
      return str;
    }

    if (stripChars == null) {
      while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
        end--;
      }
    } else if (stripChars.length() == 0) {
      return str;
    } else {
      while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND)) {
        end--;
      }
    }
    return str.substring(0, end);
  }

  /**
   * 验证email的合法性
   *
   * @param emailStr email字符串
   * @return b
   */
  public static boolean checkEmail(String emailStr) {
    String check =
        "^([a-z0-9A-Z]+[-|._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
    Pattern regex = Pattern.compile(check);
    Matcher matcher = regex.matcher(emailStr.trim());
    return matcher.matches();
  }

  /**
   * 将15812345678这样的手机号码改为 158****5678
   */
  public static String changePhone(String num) {
    if (isBlank(num)) {
      return "";
    }
    if (!checkMobile(num)) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < num.length(); i++) {
      if (i >= 3 && i <= 6) {
        sb.append("*");
      } else {
        sb.append(num.charAt(i));
      }
    }
    return sb.toString();
  }

  /**
   * 身份证验证
   *
   * @param idCard card
   * @return b
   */
  public static boolean validateIdCard(String idCard) {
    // 15位和18位身份证号码的正则表达式
    String regIdCard =
        "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$";
    Pattern p = Pattern.compile(regIdCard);
    return p.matcher(idCard).matches();
  }

  /**
   * 校验银行卡号位数方法
   *
   * @param bankCardLength l
   * @return b
   */
  public static boolean checkBankCardLength(int bankCardLength) {
    return bankCardLength >= 15 && bankCardLength <= 19;
  }

  /**
   * 校验银行卡号方法
   *
   * @param bankCard no
   * @return b
   */
  public static boolean checkBankCard(String bankCard) {
    if (bankCard.length() < 15 || bankCard.length() > 20) {
      return false;
    }
    char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
    if (bit == 'N') {
      return false;
    }
    return bankCard.charAt(bankCard.length() - 1) == bit;
  }

  /**
   * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
   *
   * @param nonCheckCodeBankCard -1的卡号
   * @return c
   */
  public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
    if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
        || !nonCheckCodeBankCard.matches("\\d+")) {
      //如果传的不是数据返回N
      return 'N';
    }
    char[] chs = nonCheckCodeBankCard.trim().toCharArray();
    int luhnSum = 0;
    for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
      int k = chs[i] - '0';
      if (j % 2 == 0) {
        k *= 2;
        k = k / 10 + k % 10;
      }
      luhnSum += k;
    }
    return (luhnSum % 10 == 0) ? '0' : (char) ((10 - luhnSum % 10) + '0');
  }

  // Splitting
  //-----------------------------------------------------------------------

  /**
   * <p>Splits the provided text into an array, using whitespace as the
   * separator. Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
   * <p/>
   * <p>The separator is not included in the returned String array.
   * Adjacent separators are treated as one separator. For more control over the split use the
   * StrTokenizer class.</p>
   * <p/>
   * <p>A <code>null</code> input String returns <code>null</code>.</p>
   * <p/>
   * <pre>
   * StringUtils.split(null)       = null
   * StringUtils.split("")         = []
   * StringUtils.split("abc def")  = ["abc", "def"]
   * StringUtils.split("abc  def") = ["abc", "def"]
   * StringUtils.split(" abc ")    = ["abc"]
   * </pre>
   *
   * @param str the String to parse, may be null
   * @return an array of parsed Strings, <code>null</code> if null String input
   */
  public static String[] split(String str) {
    return split(str, null, -1);
  }

  /**
   * <p>Splits the provided text into an array, separator specified.
   * This is an alternative to using StringTokenizer.</p>
   * <p/>
   * <p>The separator is not included in the returned String array.
   * Adjacent separators are treated as one separator. For more control over the split use the
   * StrTokenizer class.</p>
   * <p/>
   * <p>A <code>null</code> input String returns <code>null</code>.</p>
   * <p/>
   * <pre>
   * StringUtils.split(null, *)         = null
   * StringUtils.split("", *)           = []
   * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
   * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
   * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
   * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
   * </pre>
   *
   * @param str           the String to parse, may be null
   * @param separatorChar the character used as the delimiter
   * @return an array of parsed Strings, <code>null</code> if null String input
   * @since 2.0
   */
  public static String[] split(String str, char separatorChar) {
    return splitWorker(str, separatorChar, false);
  }

  /**
   * <p>Splits the provided text into an array, separators specified.
   * This is an alternative to using StringTokenizer.</p>
   * <p/>
   * <p>The separator is not included in the returned String array.
   * Adjacent separators are treated as one separator. For more control over the split use the
   * StrTokenizer class.</p>
   * <p/>
   * <p>A <code>null</code> input String returns <code>null</code>.
   * A <code>null</code> separatorChars splits on whitespace.</p>
   * <p/>
   * <pre>
   * StringUtils.split(null, *)         = null
   * StringUtils.split("", *)           = []
   * StringUtils.split("abc def", null) = ["abc", "def"]
   * StringUtils.split("abc def", " ")  = ["abc", "def"]
   * StringUtils.split("abc  def", " ") = ["abc", "def"]
   * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
   * </pre>
   *
   * @param str            the String to parse, may be null
   * @param separatorChars the characters used as the delimiters,
   *                       <code>null</code> splits on whitespace
   * @return an array of parsed Strings, <code>null</code> if null String input
   */
  public static String[] split(String str, String separatorChars) {
    return splitWorker(str, separatorChars, -1, false);
  }

  /**
   * <p>Splits the provided text into an array with a maximum length,
   * separators specified.</p>
   * <p/>
   * <p>The separator is not included in the returned String array.
   * Adjacent separators are treated as one separator.</p>
   * <p/>
   * <p>A <code>null</code> input String returns <code>null</code>.
   * A <code>null</code> separatorChars splits on whitespace.</p>
   * <p/>
   * <p>If more than <code>max</code> delimited substrings are found, the last
   * returned string includes all characters after the first <code>max - 1</code> returned strings
   * (including separator characters).</p>
   * <p/>
   * <pre>
   * StringUtils.split(null, *, *)            = null
   * StringUtils.split("", *, *)              = []
   * StringUtils.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
   * StringUtils.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
   * StringUtils.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
   * StringUtils.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
   * </pre>
   *
   * @param str            the String to parse, may be null
   * @param separatorChars the characters used as the delimiters,
   *                       <code>null</code> splits on whitespace
   * @param max            the maximum number of elements to include in the array. A zero or
   *                       negative value implies no limit
   * @return an array of parsed Strings, <code>null</code> if null String input
   */
  public static String[] split(String str, String separatorChars, int max) {
    return splitWorker(str, separatorChars, max, false);
  }

  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Performs the logic for the <code>split</code> and
   * <code>splitPreserveAllTokens</code> methods that do not return a
   * maximum array length.
   *
   * @param str               the String to parse, may be <code>null</code>
   * @param separatorChar     the separate character
   * @param preserveAllTokens if <code>true</code>, adjacent separators are treated as empty token
   *                          separators; if <code>false</code>, adjacent separators are treated as
   *                          one separator.
   * @return an array of parsed Strings, <code>null</code> if null String input
   */
  private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
    // Performance tuned for 2.0 (JDK1.4)

    if (str == null) {
      return null;
    }
    int len = str.length();
    if (len == 0) {
      return EMPTY_STRING_ARRAY;
    }
    List<String> list = new ArrayList<>();
    int i = 0, start = 0;
    boolean match = false;
    boolean lastMatch = false;
    while (i < len) {
      if (str.charAt(i) == separatorChar) {
        if (match || preserveAllTokens) {
          list.add(str.substring(start, i));
          match = false;
          lastMatch = true;
        }
        start = ++i;
        continue;
      }
      lastMatch = false;
      match = true;
      i++;
    }
    if (match || (preserveAllTokens && lastMatch)) {
      list.add(str.substring(start, i));
    }
    return list.toArray(new String[0]);
  }

  /**
   * Performs the logic for the <code>split</code> and
   * <code>splitPreserveAllTokens</code> methods that return a maximum array
   * length.
   *
   * @param str               the String to parse, may be <code>null</code>
   * @param separatorChars    the separate character
   * @param max               the maximum number of elements to include in the array. A zero or
   *                          negative value implies no limit.
   * @param preserveAllTokens if <code>true</code>, adjacent separators are treated as empty token
   *                          separators; if <code>false</code>, adjacent separators are treated as
   *                          one separator.
   * @return an array of parsed Strings, <code>null</code> if null String input
   */
  private static String[] splitWorker(String str, String separatorChars, int max,
      boolean preserveAllTokens) {
    // Performance tuned for 2.0 (JDK1.4)
    // Direct code is quicker than StringTokenizer.
    // Also, StringTokenizer uses isSpace() not isWhitespace()

    if (str == null) {
      return null;
    }
    int len = str.length();
    if (len == 0) {
      return EMPTY_STRING_ARRAY;
    }
    List<String> list = new ArrayList<>();
    int sizePlus1 = 1;
    int i = 0, start = 0;
    boolean match = false;
    boolean lastMatch = false;
    if (separatorChars == null) {
      // Null separator means use whitespace
      while (i < len) {
        if (Character.isWhitespace(str.charAt(i))) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    } else if (separatorChars.length() == 1) {
      // Optimise 1 character case
      char sep = separatorChars.charAt(0);
      while (i < len) {
        if (str.charAt(i) == sep) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    } else {
      // standard case
      while (i < len) {
        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
          if (match || preserveAllTokens) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    }
    if (match || (preserveAllTokens && lastMatch)) {
      list.add(str.substring(start, i));
    }
    return list.toArray(new String[0]);
  }

  /**
   * 保留两位小数
   *
   * @param number num
   * @return str
   */
  public static String formatNumber(Double number) {
    try {
      return new DecimalFormat("#0.00").format(number);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 保留两位小数
   *
   * @param number num
   * @return str
   */
  public static String formatNumber(String number) {
    try {
      return new DecimalFormat("#0.00").format(Double.valueOf(number));
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 字符串用比如*替代
   * @param size 大小
   * @param toReplace 被替代的字符串
   * @return s
   */
  public static String replaceAllChar(int size, String toReplace) {
    //return IntStream.range(0, size).mapToObj(i -> toReplace).collect(Collectors.joining()); //sdk api >= 24
    return repeat(toReplace, size);
  }


  /**
   * 限制EditText只允许输入两位小数:
   * @param input i
   * * @return s
   */
  public static boolean limitInputLength(String input) {
    //\\d{0,2}部分限制了整数位数, (\\.\\d{0,2})?部分限制了小数位数且为可选。
    return input.matches("^\\d{0,2}(\\.\\d{0,2})?$");
  }

  /**
   * 判断输入的内容为整数
   * @param input in
   * @return b
   */
  public static boolean checkNumInput(String input) {
    return input.matches("\\d+");
  }

  public static boolean checkInput(String input) {
    // 正则限制2位正数+2位小数
    String regex = "^\\d{2}\\.\\d{2}$";
    if (!Pattern.matches(regex, input)) {
      return false;
    }

    // 使用BigDecimal限制精度
    try {
      BigDecimal decimal = new BigDecimal(input);
      return decimal.scale() <= 2;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  /**
   * guava方法
   */
  public static String repeat(String string, int count) {
    checkNotNull(string); // eager for GWT.

    if (count <= 1) {
      checkArgument(count >= 0, "invalid count: %s", count);
      return (count == 0) ? "" : string;
    }

    // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark
    final int len = string.length();
    final long longSize = (long) len * (long) count;
    final int size = (int) longSize;
    if (size != longSize) {
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
    }

    final char[] array = new char[size];
    string.getChars(0, len, array, 0);
    int n;
    for (n = len; n < size - n; n <<= 1) {
      System.arraycopy(array, 0, array, n, n);
    }
    System.arraycopy(array, 0, array, n, size - n);
    return new String(array);
  }


  /**
   * guava的方法
   */
  public static <T> T checkNotNull(@CheckForNull T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }


  public static void checkArgument(boolean b, String errorMessageTemplate, int p1) {
    if (!b) {
      throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
    }
  }


  public static String lenientFormat(
          @CheckForNull String template, @CheckForNull @Nullable Object... args) {
    template = String.valueOf(template); // null -> "null"

    if (args == null) {
      args = new Object[] {"(Object[])null"};
    } else {
      for (int i = 0; i < args.length; i++) {
        args[i] = lenientToString(args[i]);
      }
    }

    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template, templateStart, placeholderStart);
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    }
    builder.append(template, templateStart, template.length());

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append(']');
    }

    return builder.toString();
  }

  private static String lenientToString(@CheckForNull Object o) {
    if (o == null) {
      return "null";
    }
    try {
      return o.toString();
    } catch (Exception e) {
      // Default toString() behavior - see Object.toString()
      String objectToString =
              o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
      // Logger is created inline with fixed name to avoid forcing Proguard to create another class.
      Logger.getLogger("com.google.common.base.Strings")
              .log(WARNING, "Exception during lenientFormat for " + objectToString, e);
      return "<" + objectToString + " threw " + e.getClass().getName() + ">";
    }
  }

}
