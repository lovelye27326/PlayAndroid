package com.yfy.core.util

//import android.content.pm.PackageManager
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.text.buildSpannedString
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yfy.core.Play
import com.yfy.core.util.bean.BaseBean
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * 通用方法
 * 日期： 2023年02月03日 16:25
 * 签名： 天行健，君子以自强不息；地势坤，君子以厚德载物。
 *      _              _           _     _   ____  _             _ _
 *     / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 *    / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 *   / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 *  /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/  --
 *
 * You never know what you can do until you try !
 * ----------------------------------------------------------------
 */

//跳转
inline fun <reified T : Activity> Activity.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

//跳转
inline fun <reified T : Context> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

//跳转返回
inline fun <reified T : Activity> Activity.startActivityForResult(reqCode: Int) {
    val intent = Intent(this, T::class.java)
    startActivityForResult(intent, reqCode)
}

//跳转
inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}


//跳转返回
inline fun <reified T : Activity> Activity.startActivityForResult(bundle: Bundle, reqCode: Int) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundle)
    startActivityForResult(intent, reqCode)
}

//跳转服务
inline fun <reified T : Service> Activity.startService() {
    val intent = Intent(this, T::class.java)
    startService(intent)
}

fun parsePath(path: String): String {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")
    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")
    return "$directory|$fileName|$extension"
}

//设置分割线
//fun setItemDecoration(recyclerView: RecyclerView, dp: Float) {
//    recyclerView.addItemDecoration(
//        LinearVerticalDecoration(
//            ScreenUtils.dp2px(
//                recyclerView.context,
//                dp
//            )
//        )
//    )
//}


fun CharSequence.append(suffix: CharSequence?) = buildSpannedString {
    append(this@append)
    if (suffix.isNullOrBlank()) return@buildSpannedString
    append(suffix)
}

/**
 * 滑动到指定位置
 */
fun smoothMoveToPosition(mRecyclerView: RecyclerView, position: Int) {
    // 第一个可见位置
    val firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0))
    // 最后一个可见位置
    val lastItem =
        mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.childCount - 1))
    if (position <= firstItem) {
        // 第一种可能:跳转位置在第一个可见位置之前
        mRecyclerView.smoothScrollToPosition(position)
    } else if (position <= lastItem) {
        // 第二种可能:跳转位置在第一个可见位置之后
        val movePosition = position - firstItem
        if (movePosition >= 0 && movePosition < mRecyclerView.childCount) {
            val top = mRecyclerView.getChildAt(movePosition).top
            mRecyclerView.smoothScrollBy(0, top)
        }
    } else {
        // 第三种可能:跳转位置在最后可见项之后
        mRecyclerView.smoothScrollToPosition(position)
    }
}

fun <T : Any> releasableNotNull() = ReleasableNotNull<T>()

class ReleasableNotNull<T : Any> : ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Not Initialized or released already.")
    }

    fun isInitial() = value != null

    fun release() {
        value = null
    }
}

fun <R> KProperty0<R>.release() {
    isAccessible = true
    return (getDelegate() as? ReleasableNotNull<*>)?.release()
        ?: throw IllegalAccessException("Delegate is null or is not an instance of ReleasableNotNull.")
}

fun <R> KProperty0<R>.isInitialed(): Boolean { //是否已初始化
    isAccessible = true
    return (getDelegate() as? ReleasableNotNull<*>)?.isInitial()
        ?: false
}

fun String?.default(default: String = "-"): String = if (isNullOrEmpty()) default else this


/**
 * 打印 Map，生成结构化键值对子串
 * @param space 行缩进量
 */
fun <K, V> Map<K, V>.print(space: Int = 0): String {
    //'生成当前层次的行缩进，用space个空格表示，当前层次每一行内容都需要带上缩进'
    val indent = StringBuilder().apply {
        repeat(space) { append(" ") }
    }.toString()
    return StringBuilder("\n${indent}{").also { sb ->
        this.iterator().forEach { entry ->
            //'如果值是 Map 类型，则递归调用print()生成其结构化键值对子串，否则返回值本身'
            val value = entry.value.let { v ->
                (v as? Map<*, *>)?.print("${indent}${entry.key} = ".length) ?: v.toString()
            }
            sb.append("\n\t${indent}[${entry.key}] = $value,")
        }
        sb.append("\n${indent}}")
    }.toString()
}

fun Any.ofMap(): Map<String, Any?>? {
    return this::class.takeIf { it.isData }
        ?.members?.filterIsInstance<KProperty<Any>>()
        ?.map { member ->
            val value = member.call(this).let { v ->
                //'若成员变量是data class，则递归调用ofMap()，将其转化成键值对，否则直接返回值'
                if (v::class.isData) v.ofMap()
                else v
            }
            member.name to value
        }
        ?.toMap()
}


fun Any.ofMap2(): Map<String, Any?>? {
    //'若成员变量是data class，则递归调用ofMap()，将其转化成键值对，否则直接返回值'
    return this::class.takeIf { it.isData }
        ?.members?.filterIsInstance<KProperty<Any>>()?.associate { member ->
            val value = member.call(this).let { v ->
                //'若成员变量是data class，则递归调用ofMap()，将其转化成键值对，否则直接返回值'
                if (v::class.isData) v.ofMap()
                else v
            }
            member.name to value
        }
}


open class ProtocolClickableSpan : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = Color.parseColor("#ff3370ff")
        ds.isUnderlineText = false
    }

    override fun onClick(widget: View) {}
}


class TextWatcherDsl : TextWatcher {
    private var afterTxtChanged: ((s: Editable?) -> Unit)? = null
    private var beforeTxtChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? =
        null
    private var onTxtChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? =
        null

    override fun afterTextChanged(s: Editable?) {
        afterTxtChanged?.invoke(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTxtChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTxtChanged?.invoke(s, start, before, count)
    }

    fun afterTextChanged(after: (s: Editable?) -> Unit) {
        afterTxtChanged = after
    }

    fun beforeTextChanged(before: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        beforeTxtChanged = before
    }

    fun onTextChanged(onChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
        onTxtChanged = onChanged
    }
}

inline fun EditText.onTextChange(textWatcher: TextWatcherDsl.() -> Unit): TextWatcher {
    val watcher = TextWatcherDsl().apply(textWatcher)
    addTextChangedListener(watcher)
    return watcher
}


/**
 * 添加布局变化监听器
 */
inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}


/**
 * 科学计数法转为字符串
 * max -- 保留几位小数
 */
fun Double?.convertFE(default: String = "0.0", min: Int = 1, max: Int = 4): String {
    if (this == null) return default
    val numberFormat = NumberFormat.getInstance()
    numberFormat.minimumFractionDigits = min
    numberFormat.maximumFractionDigits = max
    return numberFormat.format(this)
}


fun <T> countTimer(
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    duration: Long,
    delay: Long,
    onTimer: suspend (Long) -> T
): Job =
    callbackFlow {
        (duration - delay downTo 0 step delay).forEach {
            trySend(it)
        }
        awaitClose {
        }
    }.onStart { onTimer(duration / 1000) }
        .onEach { delay(delay) }
        .map { onTimer(it / 1000) }
        .launchIn(lifecycleCoroutineScope)


/**
 * 观察一次
 */
fun <T> LiveData<T>.observeOnce(observer: Observer<T?>) {
    observeForever(object : Observer<T?> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

/**
 * 只观察一次
 * observe(owner, object: Observer<T?> {
override fun onChanged(value: T?) {
observer(value)
removeObserver(this)
}
})
 *
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T?) -> Unit) {
    observe(owner, Observer { value ->
        observer(value)
        removeObservers(owner)
    })
}


fun getDiffDays(
    startTime: String,
    endTime: String,
    pattern: String = "yyyy-MM-dd",
    isInclude: Boolean = true
): Int {
    val startDate =
        SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT)).parse(startTime)
    val endDate =
        SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT)).parse(endTime)
    return  getDiffDays(startDate, endDate, isInclude)
}

fun getDiffDays(start: Date, end: Date, isInclude: Boolean = true): Int {
    var result = 0
    //将Date类型转换为Calendar类型
    val beforeCalendar = Calendar.getInstance()
    beforeCalendar.time = start
    val afterCalendar = Calendar.getInstance()
    afterCalendar.time = end

    //获取日期的DayOfYear（这一天在是这一年的第多少天）
    val beforeDayOfYear = beforeCalendar.get(Calendar.DAY_OF_YEAR)
    val afterDayOfYear = afterCalendar.get(Calendar.DAY_OF_YEAR)

    //获取日期的年份
    val beforeYear = beforeCalendar.get(Calendar.YEAR)
    val afterYear = afterCalendar.get(Calendar.YEAR)

    if (beforeYear == afterYear) {
        //同一年
        result = afterDayOfYear - beforeDayOfYear
    } else {
        //不同一年
        var timeDistance = 0
        for (i in beforeYear until afterYear) {
            timeDistance += if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                //闰年
                366
            } else {
                //不是闰年
                365
            }
        }
        result = timeDistance + (afterDayOfYear - beforeDayOfYear)
    }

    return if (isInclude) result + 1 else result
}



//输入效验器
interface FieldValidator<in T> {
    fun validate(input: T): Boolean
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty() // !StringUtils.isEmpty(input)
}

object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int) = input > 0
}

object DefaultLongValidator : FieldValidator<Long> {
    override fun validate(input: Long) = input > 0
}

object DefaultDoubleValidator : FieldValidator<Double> {
    override fun validate(input: Double) = input > 0.0
}

object DefaultBooleanValidator : FieldValidator<Boolean> {
    override fun validate(input: Boolean) = input
}

object DefaultAnyValidator : FieldValidator<Any?> {
    override fun validate(input: Any?) = input != null //对象非空
}

object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()

    fun <T : Any> registerValidator(kClass: KClass<T>, fieldValidator: FieldValidator<T>) {
        validators[kClass] = fieldValidator
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(kClass: KClass<T>): FieldValidator<T> =
        validators[kClass] as? FieldValidator<T>
            ?: throw  IllegalArgumentException("No validator for ${kClass.simpleName}")
}


/**
 * 手机号效验
 */
data class PhoneInfo(
    val phoneNum: String
) : BaseBean()


object DefaultPhoneValidator : FieldValidator<PhoneInfo> {
    override fun validate(input: PhoneInfo) =
        input.phoneNum.length == 11 && StringUtil.checkMobile(input.phoneNum)
}

/**
 * 身份证效验
 */
data class IdCardNoInfo(val idCardNo: String) : BaseBean()

object DefaultIdCardNoValidator : FieldValidator<IdCardNoInfo> {
    override fun validate(input: IdCardNoInfo) =
        (input.idCardNo.length == 15 || input.idCardNo.length == 18) && StringUtil.validateIdCard(
            input.idCardNo
        )
}

/**
 * 银行卡长度效验
 */
data class BankCardNoLengthInfo(val bankCardNoLength: Int) : BaseBean()

object DefaultBankCardNoLengthValidator : FieldValidator<BankCardNoLengthInfo> {
    override fun validate(input: BankCardNoLengthInfo) =
        StringUtil.checkBankCardLength(
            input.bankCardNoLength
        )
}


fun isDouble(value: String?): Boolean {
    if (value == null || !Validators[String::class].validate(value)) {
        return false
    }
    try {
        value.toDouble()
    } catch (e: Exception) {
        return false
    }
    return true
}


fun String.idCardCheck(): Boolean {
    var isIdCard = false
    if (isNotEmpty() && length == 18) {
        val coefficientArr = listOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
        var sum = 0
        for (i in coefficientArr.indices) {
            sum += Character.digit(toCharArray()[i], 10) * coefficientArr[i];
        }
        val remainderArr = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val lastArr = listOf(1, 0, 'X'.code, 9, 8, 7, 6, 5, 4, 3, 2)
        var matchDigit = ""
        for (i in remainderArr.indices) {
            var j = remainderArr[i]
            if (j == sum % 11) {
                matchDigit = lastArr[i].toString()
                if (lastArr[i] > 57) {
                    matchDigit = lastArr[i].toChar().toString()
                }
            }
        }
        isIdCard = matchDigit == substring(length - 1)
    }
    return isIdCard
}

fun getColorSpan(
    text: String,
    @ColorInt color: Int,
    start: Int,
    end: Int
): SpannableStringBuilder {
    val spannableStringBuilder = SpannableStringBuilder(text)
    spannableStringBuilder.setSpan(
        ForegroundColorSpan(color),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableStringBuilder
}


fun String.isMobPhone(): Boolean =
    "^1\\d{10}$".toRegex().matches(this)

inline fun EditText.onTextDigits(digits: Int = 4): TextWatcher {
    val watcher = TextWatcherDsl().apply {
        afterTextChanged {
            removeTextChangedListener(this)
            val originalText = it.toString()
            val formattedText =
                originalText.replace("\\s+".toRegex(), "").replace("(\\d{4})".toRegex(), "$1 ")
            setText(formattedText.trim())
            setSelection(text.length)
            addTextChangedListener(this)
        }
    }
    return watcher
}


/**
 * 上下文用Application，避免show后马上关闭页面时在有的机型如oppo小米会发生泄漏
 */
fun showToastByApp(msg: String, isShortShow: Int? = 0) {
    Toast.makeText(
        Play.context,
        msg, if (isShortShow == 0) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}


/**
 * 判断设备是否为鸿蒙设备  implementation 'androidx.preference:preference:1.1.1'
 */
//fun isHarmonyDevice(): Boolean {
//String huaweiName = PreferenceManager.getDefaultSharedPreferences(context).getString("ro.product.name", "");
//return huaweiName != null && huaweiName.contains("Huawei");
//}