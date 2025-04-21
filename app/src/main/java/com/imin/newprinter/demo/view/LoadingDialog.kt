package com.imin.newprinter.demo.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.imin.newprinter.demo.R

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
// LoadingDialog.kt
class LoadingDialog(context: Context) : Dialog(context, R.style.LoadingDialogStyle) {

    init {
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
    }

    // 设置加载文案
    fun setText(text: String): LoadingDialog {
        findViewById<TextView>(R.id.tv_text)?.text = text
        return this
    }

    // 设置动画类型
    fun setAnimationType(type: AnimationType): LoadingDialog {
        val ivLoading = findViewById<ImageView>(R.id.iv_loading)
        val dotContainer = findViewById<ViewGroup>(R.id.dot_container)

        when(type) {
            AnimationType.SPIN -> {
                ivLoading.visibility = View.VISIBLE
                dotContainer.visibility = View.GONE
                startSpinAnimation(ivLoading)
            }
            AnimationType.DOTS -> {
                ivLoading.visibility = View.GONE
                dotContainer.visibility = View.VISIBLE
                startDotAnimation(dotContainer)
            }
        }
        return this
    }

    // 旋转动画
    private fun startSpinAnimation(view: ImageView) {
        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        view.startAnimation(rotate)
    }

    // 圆点跳动动画
    private fun startDotAnimation(container: ViewGroup) {
        for (i in 0 until container.childCount) {
            val dot = container.getChildAt(i)
            val anim = ObjectAnimator.ofFloat(dot, "translationY", 0f, -20f, 0f).apply {
                duration = 600
                repeatCount = ValueAnimator.INFINITE
                startDelay = (i * 200).toLong()
            }
            anim.start()
        }
    }

    enum class AnimationType { SPIN, DOTS }
}