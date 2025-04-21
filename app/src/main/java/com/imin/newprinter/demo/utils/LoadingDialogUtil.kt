package com.imin.newprinter.demo.utils

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.view.LoadingDialog

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
object LoadingDialogUtil {
    private var loadingDialog: LoadingDialog? = null
    private var loadingCount = 0

    fun show(context: Context, config: LoadingConfig.() -> Unit = {}) {
        val configObj = LoadingConfig().apply(config)

        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(context).apply {
                setAnimationType(configObj.animationType)
                setText(configObj.text)
                window?.setDimAmount(configObj.dimAmount)
                findViewById<TextView>(R.id.tv_text)?.setTextColor(configObj.textColor)
                // 其他样式配置...
            }
        }

        if (loadingCount == 0) {
            loadingDialog?.show()
        }
        loadingCount++
    }

    fun hide() {
        if (loadingCount > 0) loadingCount--
        if (loadingCount == 0) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    class LoadingConfig {
        var text = "加载中..."
        var animationType = LoadingDialog.AnimationType.SPIN
        var textColor = Color.WHITE
        var dimAmount = 0.5f
        // 更多可配置参数...
    }
}