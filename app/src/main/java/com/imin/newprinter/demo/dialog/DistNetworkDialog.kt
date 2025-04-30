package com.imin.newprinter.demo.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.RemoteException
import android.os.SystemClock
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import com.feature.tui.util.ToastUtil
import com.imin.newprinter.demo.IminApplication
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.adapter.ListAdapter
import com.imin.newprinter.demo.databinding.DialogDistNetworkBinding
import com.imin.newprinter.demo.fragment.WifiConnectFragment
import com.imin.newprinter.demo.utils.LoadingDialogUtil
import com.imin.newprinter.demo.utils.NetworkValidator
import com.imin.newprinter.demo.utils.Utils
import com.imin.newprinter.demo.view.OnSingleClickListener
import com.imin.printer.IWirelessPrintResult
import com.imin.printer.PrinterHelper
import com.imin.printer.enums.ConnectType
import com.imin.printer.enums.IpType
import com.imin.printer.enums.WirelessConfig
import com.imin.printer.wireless.WirelessPrintStyle
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * @Author: hy
 * @date: 2025/4/29
 * @description:
 */
class DistNetworkDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = R.style.select_dialog
) : Dialog(context, themeResId) {

    private val TAG = "PrintDemo_DistNetworkDialog"
    override fun show() {
        super.show()
        initDialog()
    }

    /**
     * 初始化Dialog
     * @param width 设置宽度
     * @param height 设置高度
     * @param gravity 设置显示位置
     * @param isCancelable 是否禁用back键
     * @param  //animation 设置动画资源文件
     * @param  //isAnimation 设置是否开启动画
     */
    fun initDialog() {
        val layoutParams = window!!.attributes
        var width: Int = if (context.getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            Utils.getWidth(IminApplication.mContext)*3 / 8
        } else {
            Utils.getWidth(IminApplication.mContext) * 7 / 10
        }
        layoutParams.width = width

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        //设置显示位置
        layoutParams.gravity = Gravity.CENTER
        //设置是否屏蔽返回键与点击空白区域不关闭Dialog
        setCancelable(false)
        //设置属性
        window!!.attributes = layoutParams
    }
    var binding: DialogDistNetworkBinding? = null
    private var checkWifi = 10
    private var retry = 10
    private var isOpenEasy = false
    var isCheckPrint = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDistNetworkBinding.inflate(LayoutInflater.from(context))
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding?.pwtOpenIv?.setImageResource(R.drawable.ic_pwd_close)
        binding?.pwdEtDialog?.transformationMethod = PasswordTransformationMethod.getInstance()
        if (popList.isNotEmpty()){
            binding?.ssidSpinnerEtDialog?.setText(popList[0])
        }
        initEvent()
    }

    private fun initEvent() {

        binding?.flPwdDialog?.setOnClickListener {
            isOpenEasy = !isOpenEasy
            if (isOpenEasy) {
                binding?.pwtOpenIv?.setImageResource(R.drawable.ic_pwd_open)
                binding?.pwdEtDialog?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding?.pwtOpenIv?.setImageResource(R.drawable.ic_pwd_close)
                binding?.pwdEtDialog?.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding?.pwdEtDialog?.setSelection(binding?.pwdEtDialog?.text.toString().length)
        }

        binding?.switchStaticDialog!!.setOnCheckedChangeListener { buttonView, isChecked ->
            // 处理状态变化逻辑（如保存设置、更新UI等）
            if (isChecked) {
                // 开启状态
                binding?.lyStaticDialog!!.visibility = View.GONE
            } else {
                // 关闭状态
                binding?.lyStaticDialog!!.visibility = View.VISIBLE
            }
        }

        binding?.lyIsPrintDialog?.setOnClickListener{
            isCheckPrint = !isCheckPrint
            if (isCheckPrint){
                binding?.ivIsPrintDialog?.setImageResource(R.drawable.ic_check)
            }else{
                binding?.ivIsPrintDialog?.setImageResource(R.drawable.ic_uncheck)
            }
        }

        binding?.flCloseDialog?.setOnClickListener {
            dismiss()
        }

        binding?.flSsidDialog?.setOnClickListener { view ->
            if (popList.isNotEmpty()) {
                binding?.ssidSpinnerEtDialog?.let { showPopup(view.context, popList, it) }
            }
        }

        binding?.usbNetworkConfirmTvDialog?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {

                LoadingDialogUtil.getInstance()
                    .show(context, context.getString(R.string.toast_tips3))
                PrinterHelper.getInstance().setWirelessPrinterConfig(
                    WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                        .setConfig(ConnectType.USB.typeName),
                    object : IWirelessPrintResult.Stub() {
                        override fun onResult(i: Int, s: String?) {}
                        override fun onReturnString(s: String?) {}
                    })

                val selectedWifi = binding?.ssidSpinnerEtDialog?.text.toString().trim()
                if (selectedWifi.isNotEmpty()) {
                    if (selectedWifi.contains(" (")) {
                        val parts = selectedWifi.split(" \\(".toRegex())
                        val ssid = parts[0]
                        val bssid = parts[1].replace(")", "")
                        var pwd = binding?.pwdEtDialog?.text.toString().trim()
                        if (!bssid.contains("OPEN") && pwd.isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.toast1), Toast.LENGTH_SHORT)
                                .show()
                            LoadingDialogUtil.getInstance().hide()
                            return
                        }
                        if (bssid.contains("OPEN")){
                            pwd = ""
                        }
                        if (binding?.switchStaticDialog!!.isChecked) {
                            connectToWifi(ssid, pwd)
                        }else{
                            connectStaticWifi(ssid, pwd)
                        }
                    }
                } else {
                    LoadingDialogUtil.getInstance().hide()
                    Toast.makeText(context, context.getText(R.string.tips1), Toast.LENGTH_SHORT).show()
                }
            }
        })

//        binding?.ipConfirmTv.setOnClickListener(object : OnSingleClickListener() {
//            override fun onSingleClick(v: View?) {
//                LoadingDialogUtil.getInstance()
//                    .show(context, context.getString(R.string.toast_tips2))
//                PrinterHelper.getInstance().setWirelessPrinterConfig(
//                    WirelessPrintStyle.getWirelessPrintStyle()
//                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
//                        .setConfig(ConnectType.USB.typeName),
//                    object : IWirelessPrintResult.Stub() {
//                        override fun onResult(i: Int, s: String?) {}
//                        override fun onReturnString(s: String?) {}
//                    })
//
//                if (!outoConnect) {
//                    val ip = binding?.ipEt.text.toString().trim()
//                    if (ip.isEmpty() || !NetworkValidator.validateIP(ip)) {
//                        Toast.makeText(context, getText(R.string.ip_tips), Toast.LENGTH_SHORT)
//                            .show()
//                        LoadingDialogUtil.getInstance().hide()
//                        return
//                    }
//
//                    PrinterHelper.getInstance().setWirelessPrinterConfig(
//                        WirelessPrintStyle.getWirelessPrintStyle()
//                            .setWirelessStyle(WirelessConfig.WIFI_CONNECT_IP)
//                            .setConfig(ip),
//                        object : IWirelessPrintResult.Stub() {
//                            override fun onResult(i: Int, s: String?) {
//                                activity?.runOnUiThread {
//                                    LoadingDialogUtil.getInstance().hide()
//                                    if (i == 0) {
//                                        MainActivity.connectAddress = s
//                                        MainActivity.ipConnect = s
//                                        MainActivity.connectType = "WIFI"
//                                        MainActivity.connectContent =
//                                            binding?.wifiIPTv.text.toString().trim()
//                                        switchFragment(4)
//                                        PrinterHelper.getInstance().setWirelessPrinterConfig(
//                                            WirelessPrintStyle.getWirelessPrintStyle()
//                                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
//                                                .setConfig(ConnectType.WIFI.typeName),
//                                            object : IWirelessPrintResult.Stub() {
//                                                override fun onResult(i: Int, s: String?) {}
//                                                override fun onReturnString(s: String?) {}
//                                            })
//                                    } else {
//                                        ToastUtil.showShort(context, R.string.connect_fail)
//                                    }
//                                }
//                            }
//
//                            override fun onReturnString(s: String?) {
//                                Log.d(TAG, "WIFI_CONNECT => $s")
//                            }
//                        })
//                } else {
//                    if (!Utils.isEmpty(baseIp)) {
//                        MainActivity.connectAddress = baseIp
//                        MainActivity.ipConnect = baseIp
//                    }
//                    if (MainActivity.ipConnect.isEmpty()) {
//                        ToastUtil.showShort(context, R.string.connect_fail_tips)
//                        LoadingDialogUtil.getInstance().hide()
//                        return
//                    }
//                    PrinterHelper.getInstance().setWirelessPrinterConfig(
//                        WirelessPrintStyle.getWirelessPrintStyle()
//                            .setWirelessStyle(WirelessConfig.WIFI_AUTO_CONNECT_IP)
//                            .setConfig(MainActivity.ipConnect),
//                        object : IWirelessPrintResult.Stub() {
//                            override fun onResult(i: Int, s: String?) {
//                                activity?.runOnUiThread {
//                                    LoadingDialogUtil.getInstance().hide()
//                                    if (i == 0) {
//                                        baseIp = ""
//                                        binding?.baseIPTv.text = ""
//                                        binding?.baseIPTv.visibility = View.INVISIBLE
//                                        MainActivity.connectAddress = s
//                                        MainActivity.ipConnect = s
//                                        MainActivity.connectType = "WIFI"
//                                        MainActivity.connectContent =
//                                            binding?.wifiIPTv.text.toString().trim()
//                                        switchFragment(4)
//                                        PrinterHelper.getInstance().setWirelessPrinterConfig(
//                                            WirelessPrintStyle.getWirelessPrintStyle()
//                                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
//                                                .setConfig(ConnectType.WIFI.typeName),
//                                            object : IWirelessPrintResult.Stub() {
//                                                override fun onResult(i: Int, s: String?) {}
//                                                override fun onReturnString(s: String?) {}
//                                            })
//                                    } else {
//                                        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                            }
//
//                            override fun onReturnString(s: String?) {
//                                Log.d(TAG, "WIFI_CONNECT => $s")
//                            }
//                        })
//                }
//            }
//        })

//        binding?.getConnectIPTv.setOnClickListener(object : OnSingleClickListener() {
//            override fun onSingleClick(v: View?) {
//                LoadingDialogUtil.getInstance()
//                    .show(requireContext(), getString(R.string.toast_tips1))
//                PrinterHelper.getInstance().setWirelessPrinterConfig(
//                    WirelessPrintStyle.getWirelessPrintStyle()
//                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
//                        .setConfig(ConnectType.USB.typeName),
//                    object : IWirelessPrintResult.Stub() {
//                        override fun onResult(i: Int, s: String?) {}
//                        override fun onReturnString(s: String?) {}
//                    })
//                checkWifi = 10
//                baseIp = ""
//                binding?.baseIPTv.text = ""
//                binding?.baseIPTv.visibility = View.INVISIBLE
//                checkWifiConnect()
//            }
//        })

    }
    //配网并且设置静态ip
    //连接静态ip
     fun connectStaticWifi(ssid: String, pwd: String) {
        //设置当前动态还是静态
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE)
                .setConfig(if (binding?.switchStaticDialog!!.isChecked) IpType.DHCP.typeName else IpType.STATIC.typeName),
            object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                    Log.d(TAG, "WIFI_IP_TYPE =>$s  i=$i")
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }
            })
        //配网
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP).setSsid(ssid).setPwd(pwd),
            object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                    Log.d(TAG, "配网=>$s  i=$i")
                    if (i == 0){//下发成功 开始查询IP

                    }else{

                    }
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }
            })
        val ip: String = binding?.staticIpEtDialog!!.text.toString().trim()
        val mask: String = binding?.maskEtDialog!!.text.toString().trim()
        val gw: String = binding?.gwEtDialog!!.text.toString().trim()
        val dns: String = binding?.dnsEtDialog!!.text.toString().trim()
        if (Utils.isEmpty(ip) || Utils.isEmpty(mask) || Utils.isEmpty(gw) || Utils.isEmpty(dns)) {
            Toast.makeText(context, context.getText(R.string.ip_tips2), Toast.LENGTH_SHORT).show()
            return
        }
        if (!NetworkValidator.validateGateway(ip, gw, mask)) {
            Toast.makeText(context, context.getText(R.string.ip_tips3), Toast.LENGTH_SHORT).show()
            return
        }
        if (!NetworkValidator.validateDNS(dns)) {
            Toast.makeText(context, context.getText(R.string.ip_tips4), Toast.LENGTH_SHORT).show()
            return
        }
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP)
                .setConfig(ip), object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                    Log.d(TAG, "set ip=>$s  i=$i")

                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                    //静态ip 设置成功
//                    binding.wifiStatusTv.setText(
//                        String.format(
//                            context.getString(R.string.status_wifi),
//                            "WIFI",
//                            s
//                        )
//                    )
                }
            })
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_MASK)
                .setConfig(mask), object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }
            })
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_GW)
                .setConfig(gw), object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                    Log.d(TAG, "gw=>$s  i=$i")
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }
            })
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_DNS)
                .setConfig(dns), object : IWirelessPrintResult.Stub() {
                @Throws(RemoteException::class)
                override fun onResult(i: Int, s: String) {
                    Log.d(TAG, "dns=>$s  i=$i")
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }
            })
//        PrinterHelper.getInstance()
//            .getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
//                .setWirelessStyle(WirelessConfig.WIFI_IP), object : IWirelessPrintResult.Stub() {
//                @Throws(RemoteException::class)
//                override fun onResult(i: Int, s: String) {
//                }
//
//                @Throws(RemoteException::class)
//                override fun onReturnString(s: String) {
//                    Log.d(TAG, "ip回调==: s= $s")
//                    context.runOnUiThread(Runnable {
//                        if (!Utils.isEmpty(s) && s != "-1") {
//                            binding!!.connectTv.background =
//                                context.resources.getDrawable(R.drawable.dra_green60_corner_5)
//                            binding!!.connectNetworkTv.background =
//                                context.resources.getDrawable(R.drawable.dra_gray_corner_5)
//                            binding!!.clConnectNetwork.visibility = View.INVISIBLE
//                            binding!!.clConnectIP.visibility = View.VISIBLE
//                            binding.wifiIPTv.setText(String.format(
//                                    context.getString(R.string.status_ip),
//                                    s
//                                )
//                            )
//                        } else {
//                            ToastUtil.showShort(context, R.string.connect_wifi_tips1)
//                        }
//                    })
//                }
//            })
    }

    //配网获取动态ip
    private fun connectToWifi(ssid: String, pwd: String) {
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIFI_IP_TYPE)
                .setConfig(if (binding?.switchStaticDialog!!.isChecked) IpType.DHCP.typeName else IpType.STATIC.typeName),
            object : IWirelessPrintResult.Stub() {
                override fun onResult(i: Int, s: String?) {
                    if (i <0){
                        MainScope().launch {
                            LoadingDialogUtil.getInstance().hide()
                            ToastUtil.showShort(context, R.string.connect_fail)
                        }
                    }else{
                        PrinterHelper.getInstance().setWirelessPrinterConfig(
                            WirelessPrintStyle.getWirelessPrintStyle()
                                .setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP)
                                .setSsid(ssid)
                                .setPwd(pwd),
                            object : IWirelessPrintResult.Stub() {
                                override fun onResult(i: Int, s: String?) {
                                    Log.d(TAG,"配网回调==: i= $i ,s=>$s")
                                    if ( i == 0){
                                        //配网成功  获取ip
                                        retry = 10
                                        sendSsid()
                                    }else{
                                        MainScope().launch {
                                            //配网失败
                                            LoadingDialogUtil.getInstance().hide()
                                            ToastUtil.showShort(context, R.string.set_fail)
                                        }
                                    }

                                }
                                override fun onReturnString(s: String?) {
                                    Log.d(TAG, "配网回调==: s= $s")
                                }
                            })
                    }

                }
                override fun onReturnString(s: String?) {}
            })
    }

    private fun sendSsid() {
        Log.d(TAG, "retry=> $retry")
        PrinterHelper.getInstance().getWirelessPrinterInfo(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIFI_CONNECT_STATUS),
            object : IWirelessPrintResult.Stub() {
                override fun onResult(i: Int, s: String?) {}
                override fun onReturnString(s: String?) {
                    when (s) {
                        "0", "2" -> {
                            if (retry-- > 0) {
                                SystemClock.sleep(1000)
                                sendSsid()
                            } else {
                                MainScope().launch {
                                    ToastUtil.showShort(context, R.string.get_fail)
                                    LoadingDialogUtil.getInstance().hide()
                                }

                                retry = 10
                            }
                        }

                        "-1" -> {
                            checkWifi = 10
                            MainScope().launch {
                                LoadingDialogUtil.getInstance().hide()
                                ToastUtil.showShort(context, R.string.get_fail)
                            }
                        }

                        else -> {
                            retry = 10

                            PrinterHelper.getInstance().getWirelessPrinterInfo(
                                WirelessPrintStyle.getWirelessPrintStyle()
                                    .setWirelessStyle(WirelessConfig.WIFI_IP),
                                object : IWirelessPrintResult.Stub() {
                                    override fun onResult(i: Int, s: String?) {}
                                    override fun onReturnString(s: String?) {
                                        Log.d(TAG,"ip回调==: s= $s")
                                        MainScope().launch {
                                            if (!s.isNullOrEmpty() && s != "-1") {
                                                //配网成功 回调/弹出是否直接打印的弹框 如果是 那就调用连接接口

                                                ToastUtil.showShort(
                                                    context,
                                                    R.string.connect_wifi_tips2
                                                )
                                            } else {
                                                //IP获取失败
                                                ToastUtil.showShort(
                                                    context,
                                                    R.string.connect_wifi_tips1
                                                )
                                            }
                                            LoadingDialogUtil.getInstance().hide()
                                        }
                                    }
                                })
                        }
                    }
                }
            })
    }

    var popList: List<String> = ArrayList<String>()

    fun setPopList(list: List<String>): DistNetworkDialog? {
        this.popList = list
        return this
    }

    interface ClickListener {
        fun dismiss()
        fun cancel()
        fun sure(ip: String?,mac: String?)
    }

    var title = getContext().getString(R.string.input_text)
    fun setTitle(title: String): DistNetworkDialog? {
        this.title = title
        return this
    }

    private var popupWindow: PopupWindow? = null
    private fun showPopup(context: Context, mList: List<String>, view: View) {
        popupWindow?.dismiss()
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_list, null)
        val listView = popupView.findViewById<ListView>(R.id.listView)
        listView.adapter = ListAdapter(context, mList)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            try {
                if (position < mList.size) {
                    binding?.ssidSpinnerEtDialog?.setText(mList[position])
                }
            } catch (e: Exception) {
                Log.d(TAG, " ${e.message}")
            }
            popupWindow?.dismiss()
        }

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            animationStyle = android.R.style.Animation_Dialog
            showAsDropDown(view)
        }
    }


    override fun onBackPressed() {
        dismiss()
    }
}
