package com.yfy.play.base.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.yfy.core.util.LogUtil
import com.yfy.core.util.ScreenUtils
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 *  2020/11/17.
 */
object PermissionUtil {
    private const val REQUEST_PERMISSION = 33330 //请求权限默认请求码
    const val REQUEST_PERMISSION_SETTING = 33331 //跳转设置页默认请求码
    const val CAMERA_PERMISSION_REQUEST_CODE = 33332
    const val STORAGE_PERMISSION_REQUEST_CODE = 33333
    const val LOCATION_PERMISSION_REQUEST_CODE = 33334
    const val MICROPHONE_PERMISSION_REQUEST_CODE = 33335
    const val PHONE_PERMISSION_REQUEST_CODE = 33336
    const val PHONE_STATE_PERMISSION_REQUEST_CODE = 33337


    fun checkPermission(activity: Activity?, permission: String?): Boolean {
        if (activity == null || permission == null) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun requestPermission(activity: Activity, permission: String) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_PERMISSION)
    }

    fun showAppSettingDialog(context: Context, msg: String, requestCode: Int) {
        if (context is Activity) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("该操作需要${msg}权限，请到 “应用信息 -> 权限” 中授予！")
            builder.setPositiveButton(
                "去手动授权"
            )
            { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivityForResult(intent, requestCode)
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }
    }


    fun showAppSettingDialogFromFragment(fragment: Fragment, msg: String, requestCode: Int) {
        val context = fragment.context ?: return
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("该操作需要${msg}权限，请到 “应用信息 -> 权限” 中授予！")
        builder.setPositiveButton(
            "去手动授权"
        )
        { dialog, _ ->
            dialog.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
            fragment.startActivityForResult(intent, requestCode)
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }


    @SuppressLint("SoonBlockedPrivateApi")
    fun hookWebView() {
        val sdkInt = Build.VERSION.SDK_INT
        try {
            val factoryClass = Class.forName("android.webkit.WebViewFactory")
            val field = factoryClass.getDeclaredField("sProviderInstance")
            field.isAccessible = true
            var sProviderInstance: Any? = field.get(null)
            if (sProviderInstance != null) {
                LogUtil.i("hookWebView", "sProviderInstance isn't null")
                return
            }
            val getProviderClassMethod = if (sdkInt > 22) {
                factoryClass.getDeclaredMethod("getProviderClass")
            } else if (sdkInt == 22) {
                factoryClass.getDeclaredMethod("getFactoryClass")
            } else {
                LogUtil.i("hookWebView", "Don't need to Hook WebView")
                return
            }
            getProviderClassMethod.isAccessible = true
            val factoryProviderClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
            val delegateClass = Class.forName("android.webkit.WebViewDelegate")
            val delegateConstructor = delegateClass.getDeclaredConstructor()
            delegateConstructor.isAccessible = true
            if (sdkInt < 26) {//低于Android O版本
                var providerConstructor: Constructor<*>? = null

                try {
                    providerConstructor = factoryProviderClass.getConstructor(delegateClass)
                } catch (e: Exception) {
                    LogUtil.e("hookWebView", "Error = ${e.message}")
                }

                if (providerConstructor != null) {
                    providerConstructor.isAccessible = true
                    sProviderInstance =
                        providerConstructor.newInstance(delegateConstructor.newInstance())
                }
            } else {
                val chromiumMethodName =
                    factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD")
                chromiumMethodName.isAccessible = true
                var chromiumMethodNameStr: String? = chromiumMethodName.get(null) as String
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create"
                }
                var staticFactory: Method? = null
                try {
                    staticFactory =
                        factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass)
                } catch (e: Exception) {
                    LogUtil.e("hookWebView", "Error = ${e.message}")
                }
                if (staticFactory != null) {
                    sProviderInstance =
                        staticFactory.invoke(null, delegateConstructor.newInstance())
                }
            }
            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance)
                LogUtil.i("hookWebView", "Hook success!")
            } else {
                LogUtil.i("hookWebView", "Hook failed!")
            }
        } catch (e: Throwable) {
            LogUtil.e("hookWebView", "Error = ${e.message}")
        }
    }


    /**
     * 输入法占用上下文回收
     *
     * @param destContext ctx
     */
    fun fixInputMethodManagerLeak(destContext: Activity?) {
        if (destContext == null) {
            return
        }
        val view = destContext.currentFocus // 获取当前获得焦点的View
        if (view != null) {
            ScreenUtils.hideSoftInput(view)
            // 获取当前取得焦点的View的WindowToken
//            val token = view.windowToken
//            val imm = destContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(token, 0) // 隐藏输入法
        }
    }

}