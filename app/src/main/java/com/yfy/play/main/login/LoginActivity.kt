package com.yfy.play.main.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.yfy.core.util.*
import com.yfy.core.util.ToastUtil.showShort
import com.yfy.core.view.base.BaseActivity
import com.yfy.play.R
import com.yfy.play.base.dealer.DutyChain
import com.yfy.play.base.dealer.Handler
import com.yfy.play.base.util.PermissionUtil
import com.yfy.play.base.util.clickTrigger
import com.yfy.play.databinding.ActivityLoginBinding
import com.yfy.play.main.login.bean.Account
import com.yfy.play.main.login.bean.LoginState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private var binding by releasableNotNull<ActivityLoginBinding>()
    private val viewModel by viewModels<LoginViewModelHilt>()
    private var watcher by releasableNotNull<TextWatcher>()
    private var chain by releasableNotNull<DutyChain<String, String>>()

    //    private var chain1 by releasableNotNull<DutyChain<String, String>>()
    private var mUserName = ""
    private var mPassWord = ""
    private var mIsLogin = true


    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * EditText 的 setTransformationMethod 方法用于设置输入变换方法。这可以用于控制用户在输入文本时的行为。
     * 例如，你可以使用这个方法来实现密码输入（在输入时隐藏文本）或者电子邮件地址输入（在输入时在内容后面自动添加“@”符号等）。

    TransformationMethod 是一个接口，它定义了一个 getTransformation 方法，该方法返回一个 CharSequence，
    它表示应该显示给用户的文本，而不是原始输入的文本。

    例如，如果你想实现一个密码输入框，你可以使用 PasswordTransformationMethod，这是 TransformationMethod 的一个实现，
    它将输入的字符显示为圆点。你可以这样设置 EditText 的变换方法：

    java
    yourEditText.setTransformationMethod(new PasswordTransformationMethod());
    你也可以创建自己的 TransformationMethod 来实现更复杂的输入变换。例如，如果你想创建一个在用户输入时自动添加特定字符的变换方法，
    你可以创建一个新的 TransformationMethod 并覆盖 getTransformation 方法，如在字符串后自动添加邮箱@域名后缀

    java
    public class AutoAddTransformationMethod implements TransformationMethod {
    private final String mSuffix;

    public AutoAddTransformationMethod(String suffix) {
    mSuffix = suffix;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
    return source + mSuffix;
    }
    }
    然后，你可以这样设置 EditText 的变换方法：

    java
    yourEditText.setTransformationMethod(new AutoAddTransformationMethod("@example.com"));
     */
    override fun initView() {
        mTAG = "LoginAct"
        binding.apply {
            loginButton.clickTrigger(lifecycleScope) {
                mUserName = loginUserNameEdit?.text.toString().ifNullOrBlank { "" }
                chain =
                    DutyChain<String, String>(mUserName).apply {
                        addHandler(
                            EmptyJudgeHandler(
                                "请输入用户名",
                                loginUserNameEdit
                            )
                        ) //loginUserNameEdit
                        addHandler(MinLengthJudgeHandler("5", true, loginUserNameEdit))
                    }
                val ret = chain.execute()
                if (ret == null || !Validators[String::class].validate(ret)) {
                    return@clickTrigger
                }
                mPassWord = loginPassEdit?.text.toString().ifNullOrBlank { "" }
                chain = //chain1
                    DutyChain<String, String>(mPassWord).apply {
                        addHandler(EmptyJudgeHandler("请输入密码", loginPassEdit)) //loginPassEdit
                        addHandler(MinLengthJudgeHandler("5", false, loginPassEdit)) //loginPassEdit
                    }
                val ret1 = chain.execute()
                if (ret1 == null || !Validators[String::class].validate(ret1)) {
                    return@clickTrigger
                }
                if (!checkNetworkAvailable()) {
                    showToast(getString(R.string.no_network))
                    return@clickTrigger
                }
                commitLoginOrRegister()
            }
            loginTvRegister.clickTrigger(lifecycleScope) {
                flipAnimatorXViewShow(loginInputElements) //反转动画
            }
            loginPassClear?.clickTrigger(lifecycleScope) {
                loginPassEdit?.setText("")
            }
            loginPassVisible?.clickTrigger(lifecycleScope) {
                val transformationMethod = loginPassEdit?.transformationMethod
                if (transformationMethod is PasswordTransformationMethod) {
                    loginPassEdit!!.transformationMethod =
                        HideReturnsTransformationMethod.getInstance() //重置输入变换方法
                    loginPassVisible.setColorFilter(getColor(R.color.colorLoading))
                } else {
                    loginPassEdit!!.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    loginPassVisible.setColorFilter(getColor(R.color.text_color_black))
                }
                loginPassEdit.setSelection(
                    loginPassEdit.text.toString().trim().length
                )
            }

            loginPassEdit?.let {
                watcher = it.onTextChange {
                    afterTextChanged { s ->
                        binding.loginPassClear?.isVisible = !s.isNullOrEmpty()
                    }
                }
            }

            loginPassEdit?.transformationMethod =
                PasswordTransformationMethod.getInstance() //设置密码输入变换方法
        }


        viewModel.state.observe(this) {
            when (it) {
                LoginState.Logging -> { //Logging是object声明的，不用is判断
                    toProgressVisible(true)
                }
                is LoginState.LoginSuccess -> {
                    toProgressVisible(false)
                    finish()
                }
                is LoginState.LoginError -> {
                    toProgressVisible(false)
                    LogUtil.i(mTAG, "err: ${it.errStr}")
                    if (it.errStr.contains("|")) {
                        val toastStr = it.errStr.split("|")[0]
                        showToast(toastStr)
                    } else {
                        showToast(it.errStr)
                    }
                }
                LoginState.Finished -> { //Logging是object声明的，不用is判断
                    toProgressVisible(false)
                }
                else -> {}
            }
        }


//        viewModel.stateData.observe(this) {
//            it?.let { data ->
//                when (data) {
//                    LoaderState.STATE_LOADING -> {
//                        toProgressVisible(true)
//                    }
//                    LoaderState.STATE_SUCCESS -> {
//                        toProgressVisible(false)
//                        finish()
//                    }
//                    LoaderState.STATE_NET_ERROR, LoaderState.STATE_SOURCE_ERROR -> {
//                        toProgressVisible(false)
//                    }
//                }
//            }
//        }
    }

    private fun commitLoginOrRegister() {
//        if (!judge()) return
        viewModel.toLoginOrRegister(Account(mUserName, mPassWord, mIsLogin))
    }

    private fun updateState() {
        binding.loginTvRegister.text =
            if (mIsLogin) getString(R.string.return_login) else getString(R.string.register_account)
        binding.loginButton.text =
            if (mIsLogin) getString(R.string.register_account) else getString(R.string.login)
        mIsLogin = !mIsLogin
    }

    private fun flipAnimatorXViewShow(view: View) {
        val animator1 = ObjectAnimator.ofFloat(view, "rotationY", 0f, if (mIsLogin) 90f else -90f)
        val animator2 = ObjectAnimator.ofFloat(view, "rotationY", if (mIsLogin) -90f else 90f, 0f)
        animator2.interpolator = OvershootInterpolator(2.0f)
        animator1.setDuration(700).start()
        animator1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animator2.setDuration(700).start()
                updateState()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun judge(): Boolean {
        if (TextUtils.isEmpty(mUserName) || mUserName.length < 5) {
            binding.loginUserNameEdit?.error =
                getString(R.string.enter_name_format) //输入框设置error提示，偶尔会看不到提示error提示框，不太好
            return false
        }
        if (TextUtils.isEmpty(mPassWord) || mPassWord.length < 5) {
            binding.loginPassEdit?.error = getString(R.string.enter_password_format)
            return false
        }

        return true
    }

    private fun toProgressVisible(visible: Boolean) {
        binding.loginProgressBar.isVisible = visible
        binding.loginInputElements.isVisible = !visible
    }


    /**
     * 判空处理器
     */
    private class EmptyJudgeHandler(
        private val tip: String,
        private val editText: EditText? = null
    ) : Handler<String, String> {
        override fun handle(data: String, chain: Handler.Chain<String, String>): String? {
            val filterString = data.let { //进行处理
                LogUtil.i("EmptyJudgeHandler", "input: $it")
                if (!Validators[String::class].validate(it)) {
                    if (editText != null)
                        ThreadUtils.getMainHandler().post {
                            editText.error = tip
                        }
                    showShort(tip)
                    return ""
                }
                it
            }
            return chain.next(filterString)
        }
    }


    /**
     * 最小长度输入处理器
     */
    private class MinLengthJudgeHandler(
        private val maxLength: String = "5",
        private val isUserName: Boolean = true,
        private val editText: EditText? = null
    ) : Handler<String, String> {
        override fun handle(data: String, chain: Handler.Chain<String, String>): String? {
            val filterString = data.let { //进行处理
                LogUtil.i("MinLengthJudgeHandler", "input: $it")
                if (it.length < maxLength.toInt()) { // length 比较，当用it.toInt()可能报异常，clickTrigger里可用SpiderMan显示
                    val tip = if (isUserName) {
                        "用户名长度不能小于$maxLength"
                    } else {
                        "密码长度不能小于$maxLength"
                    }
                    if (editText != null)
                        ThreadUtils.getMainHandler().post {
                            editText.error = tip
                        }
                    showShort(tip)
                    return ""
                }
                it
            }
            return chain.next(filterString)
        }
    }


    override fun onDestroy() {
        if (::watcher.isInitialed()) {
            binding.loginPassEdit?.removeTextChangedListener(watcher)
            ::watcher.release()
        }
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        if (::chain.isInitialed()) {
            chain.clear()
            ::chain.release()
        }
//        if (::chain1.isInitialed()) {
//            chain1.clear()
//            ::chain1.release()
//        }
        super.onDestroy()
        PermissionUtil.fixInputMethodManagerLeak(this)
    }


}
