package com.yfy.play

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.yfy.core.util.BarUtil
import com.yfy.core.util.showShortToast
import com.yfy.play.databinding.ActivityWelcomeBinding
import com.yfy.play.main.MainActivity

class WelcomeActivity : AppCompatActivity(),
    View.OnClickListener {

    private lateinit var binding: ActivityWelcomeBinding
    private var exitTime: Long = 0
    private var animationTime: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtil.setStatusBarLightMode(this, true)
        //处理首次安装点击打开切到后台,点击桌面图标再回来重启的问题及通过应用宝唤起在特定条件下重走逻辑的问题
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish()
            return
        }
        setContentView(getLayoutView())
        initView()
    }

    private fun getLayoutView(): View {
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }


    private fun initAnimation() {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = animationTime
        rotateAnimation.fillAfter = true
        val scaleAnimation = ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = animationTime
        scaleAnimation.fillAfter = true
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = animationTime
        alphaAnimation.fillAfter = true
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(alphaAnimation)
        binding.ivWelcomeBg.startAnimation(animationSet)
        animationSet.setAnimationListener(animationListener)
    }


    @SuppressLint("SetTextI18n")
    fun initView() {
        initAnimation()
        binding.ivWelcomeBg.setOnClickListener(this)
    }

    private val animationListener = object : Animation.AnimationListener {
        /**
         * 动画开始的时候执行
         * @param animation
         */
        override fun onAnimationStart(animation: Animation) {}

        /**
         * 动画结束的时候执行
         * @param animation
         */
        override fun onAnimationEnd(animation: Animation) {
            //跳转到登陆界面
            jump()
        }

        /**
         * 动画重复的时候执行
         * @param animation
         */
        override fun onAnimationRepeat(animation: Animation) {}
    }

    private fun jump() {
        MainActivity.actionStart(this)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivWelcomeBg -> {
                jump()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showShortToast(R.string.exit_program)
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

}
