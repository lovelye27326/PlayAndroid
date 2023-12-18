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
import com.yfy.core.util.LogUtil
import com.yfy.core.util.checkNetworkAvailable
import com.yfy.core.util.showToast
import com.yfy.core.view.base.BaseActivity
import com.yfy.play.R
import com.yfy.play.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener, TextWatcher {

    private lateinit var binding: ActivityLoginBinding

    //    private val viewModel by viewModels<LoginViewModel>()
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
        binding.loginButton.setOnClickListener(this)
        binding.loginTvRegister.setOnClickListener(this)
        binding.loginPassNumberClear.setOnClickListener(this)
        binding.loginPassNumberVisible.setOnClickListener(this)
        binding.loginPassNumberEdit.addTextChangedListener(this)
        binding.loginPassNumberEdit.transformationMethod =
            PasswordTransformationMethod.getInstance()


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


    override fun onClick(v: View) {
        when (v.id) {
            R.id.loginTvRegister -> {
                flipAnimatorXViewShow(binding.loginInputElements)
            }
            R.id.loginButton -> {
                loginOrRegister()
            }
            R.id.loginPassNumberClear -> {
                binding.loginPassNumberEdit.setText("")
            }
            R.id.loginPassNumberVisible -> {
                val transformationMethod = binding.loginPassNumberEdit.transformationMethod
                if (transformationMethod is PasswordTransformationMethod) {
                    binding.loginPassNumberEdit.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.loginPassNumberVisible.setColorFilter(getColor(R.color.colorLoading))
                } else {
                    binding.loginPassNumberEdit.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.loginPassNumberVisible.setColorFilter(getColor(R.color.text_color_black))
                }
                binding.loginPassNumberEdit.setSelection(
                    binding.loginPassNumberEdit.text.toString().trim().length
                )
            }
        }
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
        mUserName = binding.loginUserNumberEdit.text.toString()
        mPassWord = binding.loginPassNumberEdit.text.toString()
        if (TextUtils.isEmpty(mUserName) || mUserName.length < 5) {
            binding.loginUserNumberEdit.error =
                getString(R.string.enter_name_format) //输入框设置error提示，偶尔看不到提示error提示框，不太好
            return false
        }
        if (TextUtils.isEmpty(mPassWord) || mPassWord.length < 5) {
            binding.loginPassNumberEdit.error = getString(R.string.enter_password_format)
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
        binding.loginPassNumberClear.isVisible = !s.isNullOrEmpty()
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }


    /**
     * 使用useCase的挂起登录函数
     */
//    private suspend fun login() {
//        val useCase = UseCase(GetLoginProjects(PlayAndroidNetwork.loginService))
//        useCase.getLoginProjects(mUserName, mPassWord)
//    }
}
