package com.yfy.play.main.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.yfy.core.util.LogUtil
import com.yfy.core.util.checkNetworkAvailable
import com.yfy.core.util.showToast
import com.yfy.core.view.base.BaseActivity
import com.yfy.play.R
import com.yfy.play.base.util.*
import com.yfy.play.databinding.ActivityLoginBinding
import com.yfy.play.main.login.bean.Account
import com.yfy.play.main.login.bean.LoginState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity(), TextWatcher {
    private var binding by releasableNotNull<ActivityLoginBinding>()
    private val viewModel by viewModels<LoginViewModelHilt>()
    private var mUserName = ""
    private var mPassWord = ""
    private var mIsLogin = true


    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mTAG = "LoginAct"
        binding.apply {
            loginButton.clickTrigger(lifecycleScope) {
                loginOrRegister()
            }
            loginTvRegister.clickTrigger(lifecycleScope) {
                flipAnimatorXViewShow(loginInputElements)
            }
            loginPassClear?.clickTrigger(lifecycleScope) {
                loginPassEdit?.setText("")
            }
            loginPassVisible?.clickTrigger(lifecycleScope) {
                val transformationMethod = loginPassEdit?.transformationMethod
                if (transformationMethod is PasswordTransformationMethod) {
                    loginPassEdit!!.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
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
            loginPassEdit?.addTextChangedListener(this@LoginActivity)
            loginPassEdit?.transformationMethod =
                PasswordTransformationMethod.getInstance()
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

    private fun loginOrRegister() {
        if (!judge()) return
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
        mUserName = binding.loginUserNameEdit?.text.toString()
        mPassWord = binding.loginPassEdit?.text.toString()
        if (TextUtils.isEmpty(mUserName) || mUserName.length < 5) {
            binding.loginUserNameEdit?.error =
                getString(R.string.enter_name_format) //输入框设置error提示，偶尔会看不到提示error提示框，不太好
            return false
        }
        if (TextUtils.isEmpty(mPassWord) || mPassWord.length < 5) {
            binding.loginPassEdit?.error = getString(R.string.enter_password_format)
            return false
        }
        if (!checkNetworkAvailable()) {
            showToast(getString(R.string.no_network))
            return false
        }
        return true
    }

    private fun toProgressVisible(visible: Boolean) {
        binding.loginProgressBar.isVisible = visible
        binding.loginInputElements.isVisible = !visible
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        binding.loginPassClear?.isVisible = !s.isNullOrEmpty()
    }

//    companion object {
//        fun actionStart(context: Context) {
//            val intent = Intent(context, LoginActivity::class.java)
//            context.startActivity(intent)
//        }
//    }


    override fun onDestroy() {
        if (::binding.isInitialed()) {
            ::binding.release()
        }
        super.onDestroy()
        PermissionUtil.fixInputMethodManagerLeak(this)
    }


}
