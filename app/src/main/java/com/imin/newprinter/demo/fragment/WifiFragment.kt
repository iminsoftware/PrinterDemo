package com.imin.newprinter.demo.fragment

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.feature.tui.util.ToastUtil
import com.imin.newprinter.demo.MainActivity
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.adapter.ListAdapter
import com.imin.newprinter.demo.callback.SwitchFragmentListener
import com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding
import com.imin.newprinter.demo.utils.ExecutorServiceManager
import com.imin.newprinter.demo.utils.LoadingDialogUtil
import com.imin.newprinter.demo.utils.NetworkValidator
import com.imin.newprinter.demo.utils.Utils
import com.imin.newprinter.demo.utils.WifiScannerSingleton
import com.imin.newprinter.demo.view.OnSingleClickListener
import com.imin.printer.IWirelessPrintResult
import com.imin.printer.PrinterHelper
import com.imin.printer.enums.ConnectType
import com.imin.printer.enums.IpType
import com.imin.printer.enums.WirelessConfig
import com.imin.printer.wireless.WirelessPrintStyle
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WifiFragment : BaseFragment(), WifiScannerSingleton.WifiListListener {

    private val TAG = "PrintDemo_WifiFragment"
    private lateinit var binding: FragmentWifiConnectBinding
    private var list = ArrayList<String>()
    private var baseIp = ""
    private var isOpenEasy = false
    private var outoConnect = true
    private var adapter: ArrayAdapter<String>? = null
    private var isVisibleToView = false
    public lateinit var wifiScanner: WifiScannerSingleton
    private var checkWifi = 10
    private var retry = 10
    private var fragmentListener: SwitchFragmentListener? = null
    private var popupWindow: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWifiConnectBinding.inflate(inflater)
        wifiScanner = WifiScannerSingleton.getInstance(requireContext())
        initView()
        initData()
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d(TAG, "setUserVisibleHint: ${isVisibleToView}")
        isVisibleToView = isVisibleToUser
        if (isVisibleToUser && isResumed) {
            initData()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ${isVisible}")
    }

    private fun startWifiScan() {
        wifiScanner.stopWifiScan()
        wifiScanner.startWifiScan(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        if (list.isNotEmpty()) {
            binding.ssidSpinner.text = list[0]
        }

        binding.ssidSpinner.setOnClickListener { view ->
            if (list.isNotEmpty()) {
                showPopup(view.context, list, view)
            }
        }

        binding.autoConnectIv.setImageResource(R.drawable.ic_check)
        binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close)
        binding.pwdEt.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.ipEt.isEnabled = false

        binding.connectNetworkTv.setOnClickListener {
            binding.connectNetworkTv.background =
                requireContext().getDrawable(R.drawable.dra_green60_corner_5)
            binding.connectTv.background =
                requireContext().getDrawable(R.drawable.dra_gray_corner_5)
            binding.clConnectNetwork.visibility = View.VISIBLE
            binding.clConnectIP.visibility = View.INVISIBLE
        }

        binding.connectTv.setOnClickListener {
            binding.connectTv.background =
                requireContext().getDrawable(R.drawable.dra_green60_corner_5)
            binding.connectNetworkTv.background =
                requireContext().getDrawable(R.drawable.dra_gray_corner_5)
            binding.clConnectNetwork.visibility = View.INVISIBLE
            binding.clConnectIP.visibility = View.VISIBLE
        }

        binding.autoLy.setOnClickListener {
            binding.autoConnectIv.setImageResource(R.drawable.ic_check)
            binding.ipIv.setImageResource(R.drawable.ic_uncheck)
            binding.ipEt.isEnabled = false
            outoConnect = true
            binding.getConnectIPTv.isEnabled = true
            binding.getConnectIPTv.background = resources.getDrawable(R.drawable.dra_blue_corner_5)
        }

        binding.ipLy.setOnClickListener {
            binding.ipIv.setImageResource(R.drawable.ic_check)
            binding.autoConnectIv.setImageResource(R.drawable.ic_uncheck)
            binding.ipEt.isEnabled = true
            outoConnect = false
            binding.getConnectIPTv.isEnabled = false
            binding.getConnectIPTv.background = resources.getDrawable(R.drawable.dra_gray_corner_5)
        }

        binding.flPwd.setOnClickListener {
            isOpenEasy = !isOpenEasy
            if (isOpenEasy) {
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_open)
                binding.pwdEt.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close)
                binding.pwdEt.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.pwdEt.setSelection(binding.pwdEt.text.length)
        }

        binding.networkConfirmTv.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                LoadingDialogUtil.getInstance()
                    .show(requireContext(), getString(R.string.toast_tips3))
                PrinterHelper.getInstance().setWirelessPrinterConfig(
                    WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                        .setConfig(ConnectType.USB.typeName),
                    object : IWirelessPrintResult.Stub() {
                        override fun onResult(i: Int, s: String?) {}
                        override fun onReturnString(s: String?) {}
                    })

                val selectedWifi = binding.ssidSpinner.text.toString().trim()
                if (selectedWifi.isNotEmpty()) {
                    if (selectedWifi.contains(" (")) {
                        val parts = selectedWifi.split(" \\(".toRegex())
                        val ssid = parts[0]
                        val bssid = parts[1].replace(")", "")
                        var pwd = binding.pwdEt.text.toString().trim()
                        if (!bssid.contains("OPEN") && pwd.isEmpty()) {
                            Toast.makeText(context, getString(R.string.toast1), Toast.LENGTH_SHORT)
                                .show()
                            LoadingDialogUtil.getInstance().hide()
                            return
                        }
                        if (bssid.contains("OPEN")){
                            pwd = ""
                        }
                        if (binding.swichStatic.isChecked) {

                            connectToWifi(ssid, pwd)
                        }
                    }
                } else {
                    LoadingDialogUtil.getInstance().hide()
                    Toast.makeText(context, getText(R.string.tips1), Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.ipConfirmTv.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                LoadingDialogUtil.getInstance()
                    .show(requireContext(), getString(R.string.toast_tips2))
                PrinterHelper.getInstance().setWirelessPrinterConfig(
                    WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                        .setConfig(ConnectType.USB.typeName),
                    object : IWirelessPrintResult.Stub() {
                        override fun onResult(i: Int, s: String?) {}
                        override fun onReturnString(s: String?) {}
                    })

                if (!outoConnect) {
                    val ip = binding.ipEt.text.toString().trim()
                    if (ip.isEmpty() || !NetworkValidator.validateIP(ip)) {
                        Toast.makeText(context, getText(R.string.ip_tips), Toast.LENGTH_SHORT)
                            .show()
                        LoadingDialogUtil.getInstance().hide()
                        return
                    }

                    PrinterHelper.getInstance().setWirelessPrinterConfig(
                        WirelessPrintStyle.getWirelessPrintStyle()
                            .setWirelessStyle(WirelessConfig.WIFI_CONNECT_IP)
                            .setConfig(ip),
                        object : IWirelessPrintResult.Stub() {
                            override fun onResult(i: Int, s: String?) {
                                activity?.runOnUiThread {
                                    LoadingDialogUtil.getInstance().hide()
                                    if (i == 0) {
                                        MainActivity.connectAddress = s
                                        MainActivity.ipConnect = s
                                        MainActivity.connectType = "WIFI"
                                        MainActivity.connectContent =
                                            binding.wifiIPTv.text.toString().trim()
                                        switchFragment(4)
                                        PrinterHelper.getInstance().setWirelessPrinterConfig(
                                            WirelessPrintStyle.getWirelessPrintStyle()
                                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                                                .setConfig(ConnectType.WIFI.typeName),
                                            object : IWirelessPrintResult.Stub() {
                                                override fun onResult(i: Int, s: String?) {}
                                                override fun onReturnString(s: String?) {}
                                            })
                                    } else {
                                        ToastUtil.showShort(context, R.string.connect_fail)
                                    }
                                }
                            }

                            override fun onReturnString(s: String?) {
                                Log.d(TAG, "WIFI_CONNECT => $s")
                            }
                        })
                } else {
                    if (!Utils.isEmpty(baseIp)) {
                        MainActivity.connectAddress = baseIp
                        MainActivity.ipConnect = baseIp
                    }
                    if (MainActivity.ipConnect.isEmpty()) {
                        ToastUtil.showShort(context, R.string.connect_fail_tips)
                        LoadingDialogUtil.getInstance().hide()
                        return
                    }
                    PrinterHelper.getInstance().setWirelessPrinterConfig(
                        WirelessPrintStyle.getWirelessPrintStyle()
                            .setWirelessStyle(WirelessConfig.WIFI_AUTO_CONNECT_IP)
                            .setConfig(MainActivity.ipConnect),
                        object : IWirelessPrintResult.Stub() {
                            override fun onResult(i: Int, s: String?) {
                                activity?.runOnUiThread {
                                    LoadingDialogUtil.getInstance().hide()
                                    if (i == 0) {
                                        baseIp = ""
                                        binding.baseIPTv.text = ""
                                        binding.baseIPTv.visibility = View.INVISIBLE
                                        MainActivity.connectAddress = s
                                        MainActivity.ipConnect = s
                                        MainActivity.connectType = "WIFI"
                                        MainActivity.connectContent =
                                            binding.wifiIPTv.text.toString().trim()
                                        switchFragment(4)
                                        PrinterHelper.getInstance().setWirelessPrinterConfig(
                                            WirelessPrintStyle.getWirelessPrintStyle()
                                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                                                .setConfig(ConnectType.WIFI.typeName),
                                            object : IWirelessPrintResult.Stub() {
                                                override fun onResult(i: Int, s: String?) {}
                                                override fun onReturnString(s: String?) {}
                                            })
                                    } else {
                                        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            override fun onReturnString(s: String?) {
                                Log.d(TAG, "WIFI_CONNECT => $s")
                            }
                        })
                }
            }
        })

        binding.getConnectIPTv.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                LoadingDialogUtil.getInstance()
                    .show(requireContext(), getString(R.string.toast_tips1))
                PrinterHelper.getInstance().setWirelessPrinterConfig(
                    WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                        .setConfig(ConnectType.USB.typeName),
                    object : IWirelessPrintResult.Stub() {
                        override fun onResult(i: Int, s: String?) {}
                        override fun onReturnString(s: String?) {}
                    })
                checkWifi = 10
                baseIp = ""
                binding.baseIPTv.text = ""
                binding.baseIPTv.visibility = View.INVISIBLE
                checkWifiConnect()
            }
        })

        binding.viewTitle.setRightCallback { switchFragment(100) }
        binding.btDisconnect.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                disConnect()
            }
        })
    }

    private fun connectToWifi(ssid: String, pwd: String) {
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIFI_IP_TYPE)
                .setConfig(if (binding.swichStatic.isChecked) IpType.DHCP.typeName else IpType.STATIC.typeName),
            object : IWirelessPrintResult.Stub() {
                override fun onResult(i: Int, s: String?) {
                    activity?.runOnUiThread {
                        if (i < 0) {
                            LoadingDialogUtil.getInstance().hide()
                            ToastUtil.showShort(context, R.string.connect_fail)
                        } else {
                            PrinterHelper.getInstance().setWirelessPrinterConfig(
                                WirelessPrintStyle.getWirelessPrintStyle()
                                    .setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP)
                                    .setSsid(ssid)
                                    .setPwd(pwd),
                                object : IWirelessPrintResult.Stub() {
                                    override fun onResult(i: Int, s: String?) {
                                        Log.d(TAG,
                                            "配网回调==: i= $i ,s=>$s"
                                        )
                                        if (i == 0) {
                                            activity?.runOnUiThread {
                                                binding.wifiStatusTv.text = String.format(
                                                    getString(R.string.status_wifi),
                                                    "WIFI",
                                                    getString(R.string.connected)
                                                )
                                            }
                                            retry = 10
                                            sendSsid()
                                        } else {
                                            activity?.runOnUiThread {
                                                LoadingDialogUtil.getInstance().hide()
                                                binding.wifiStatusTv.text = String.format(
                                                    getString(R.string.status_wifi),
                                                    "WIFI",
                                                    getString(R.string.un_connected)
                                                )
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
                                activity?.runOnUiThread {
                                    binding.wifiStatusTv.text = String.format(
                                        getString(R.string.status_wifi),
                                        "WIFI",
                                        getString(R.string.un_connected)
                                    )
                                    binding.wifiIPTv.text =
                                        String.format(getString(R.string.status_ip), "------")
                                    binding.connectNetworkTv.background =
                                        requireContext().getDrawable(R.drawable.dra_green60_corner_5)
                                    binding.connectTv.background =
                                        requireContext().getDrawable(R.drawable.dra_gray_corner_5)
                                    binding.clConnectNetwork.visibility = View.VISIBLE
                                    binding.clConnectIP.visibility = View.INVISIBLE
                                    LoadingDialogUtil.getInstance().hide()
                                }
                                retry = 10
                            }
                        }

                        "-1" -> {
                            checkWifi = 10
                            activity?.runOnUiThread {
                                LoadingDialogUtil.getInstance().hide()
                                ToastUtil.showShort(context, R.string.get_fail)
                            }
                        }

                        else -> {
                            retry = 10
                            activity?.runOnUiThread {
                                binding.wifiStatusTv.text = String.format(
                                    getString(R.string.status_wifi),
                                    "WIFI",
                                    getString(R.string.connected)
                                )
                            }
                            PrinterHelper.getInstance().getWirelessPrinterInfo(
                                WirelessPrintStyle.getWirelessPrintStyle()
                                    .setWirelessStyle(WirelessConfig.WIFI_IP),
                                object : IWirelessPrintResult.Stub() {
                                    override fun onResult(i: Int, s: String?) {}
                                    override fun onReturnString(s: String?) {
                                        Log.d(TAG,
                                            "ip回调==: s= $s"
                                        )
                                        activity?.runOnUiThread {
                                            if (!s.isNullOrEmpty() && s != "-1") {
                                                baseIp = ""
                                                binding.baseIPTv.visibility = View.INVISIBLE
                                                binding.baseIPTv.text = ""
                                                MainActivity.ipConnect = s
                                                MainActivity.connectAddress = s
                                                binding.connectTv.background =
                                                    requireContext().getDrawable(R.drawable.dra_green60_corner_5)
                                                binding.connectNetworkTv.background =
                                                    requireContext().getDrawable(R.drawable.dra_gray_corner_5)
                                                binding.clConnectNetwork.visibility = View.INVISIBLE
                                                binding.clConnectIP.visibility = View.VISIBLE
                                                binding.wifiIPTv.text =
                                                    String.format(getString(R.string.status_ip), s)
                                                ToastUtil.showShort(
                                                    context,
                                                    R.string.connect_wifi_tips2
                                                )
                                            } else {
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

    private fun initData() {
        baseIp = ""
        binding.baseIPTv.text = ""
        binding.baseIPTv.visibility = View.INVISIBLE
        // 使用 lifecycleScope 确保生命周期安全
        lifecycleScope.launchWhenResumed {
            try {
                Log.e(TAG, "startWifiScan 开始}")
                startWifiScan()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting wifi scan: ${e.message}")
            }
        }

        if (MainActivity.ipConnect.isEmpty()) {
            PrinterHelper.getInstance().getWirelessPrinterInfo(
                WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.CURRENT_CONNECT_WIFI_IP),
                object : IWirelessPrintResult.Stub() {
                    override fun onResult(i: Int, s: String?) {
                        Log.d(TAG,
                            "initData==CURRENT_CONNECT_WIFI_IP: $s  ,i=>  $i"
                        )
                        activity?.runOnUiThread {
                            if (i == 0 && s != null) {
                                MainActivity.ipConnect = s
                                MainActivity.connectAddress = s
                                MainActivity.connectType = "WIFI"
                                binding.wifiStatusTv.text = String.format(
                                    getString(R.string.status_wifi),
                                    "WIFI",
                                    getString(R.string.connected)
                                )
                                binding.wifiIPTv.text =
                                    String.format(getString(R.string.status_ip), s)
                                LoadingDialogUtil.getInstance().hide()
                                binding.connectTv.background =
                                    requireContext().getDrawable(R.drawable.dra_green60_corner_5)
                                binding.connectNetworkTv.background =
                                    requireContext().getDrawable(R.drawable.dra_gray_corner_5)
                                binding.clConnectNetwork.visibility = View.INVISIBLE
                                binding.clConnectIP.visibility = View.VISIBLE
                            } else {
                                binding.wifiStatusTv.text = String.format(
                                    getString(R.string.status_wifi),
                                    "WIFI",
                                    getString(R.string.un_connected)
                                )
                                binding.wifiIPTv.text =
                                    String.format(getString(R.string.status_ip), "------")
                                binding.connectNetworkTv.background =
                                    requireContext().getDrawable(R.drawable.dra_green60_corner_5)
                                binding.connectTv.background =
                                    requireContext().getDrawable(R.drawable.dra_gray_corner_5)
                                binding.clConnectNetwork.visibility = View.VISIBLE
                                binding.clConnectIP.visibility = View.INVISIBLE
                            }
                        }
                    }

                    override fun onReturnString(s: String?) {}
                })
        } else {
            updateUi()
        }

        updatePrinterStatus(PrinterHelper.getInstance().printerStatus)
    }

    private fun disConnect() {
        PrinterHelper.getInstance().setWirelessPrinterConfig(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.DISCONNECT_WIFI),
            object : IWirelessPrintResult.Stub() {
                override fun onResult(i: Int, s: String?) {
                    Log.d(TAG, "DISCONNECT_WIFI==:  $s")
                    activity?.runOnUiThread {
                        binding.wifiStatusTv.text = String.format(
                            getString(R.string.status_wifi),
                            "WIFI",
                            getString(R.string.un_connected)
                        )
                        binding.wifiIPTv.text =
                            String.format(getString(R.string.status_ip), "-------")
                        MainActivity.ipConnect = ""
                    }
                }

                override fun onReturnString(s: String?) {}
            })
    }

    public fun updateUi() {
        activity?.runOnUiThread {
            binding.wifiStatusTv.text = String.format(
                getString(R.string.status_wifi),
                "WIFI",
                getString(R.string.connected)
            )
            binding.wifiIPTv.text =
                String.format(getString(R.string.status_ip), MainActivity.ipConnect)
            binding.connectTv.background =
                requireContext().getDrawable(R.drawable.dra_green60_corner_5)
            binding.connectNetworkTv.background =
                requireContext().getDrawable(R.drawable.dra_gray_corner_5)
            binding.clConnectNetwork.visibility = View.INVISIBLE
            binding.clConnectIP.visibility = View.VISIBLE
        }
    }

    private fun checkWifiConnect() {
        PrinterHelper.getInstance().getWirelessPrinterInfo(
            WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIFI_CONNECT_STATUS),
            object : IWirelessPrintResult.Stub() {
                override fun onResult(i: Int, s: String?) {}
                override fun onReturnString(s: String?) {
                    when (s) {
                        "0", "2" -> {
                            if (checkWifi-- > 0) {
                                SystemClock.sleep(2000)
                                checkWifiConnect()
                            } else {
                                activity?.runOnUiThread {
                                    LoadingDialogUtil.getInstance().hide()
                                    ToastUtil.showShort(context, R.string.get_fail)
                                }
                                checkWifi = 10
                            }
                        }

                        "-1" -> {
                            checkWifi = 10
                            activity?.runOnUiThread {
                                ToastUtil.showShort(context, R.string.get_fail)
                                LoadingDialogUtil.getInstance().hide()
                            }
                        }

                        else -> {
                            checkWifi = 10
                            activity?.runOnUiThread {
                                binding.wifiStatusTv.text = String.format(
                                    getString(R.string.status_wifi),
                                    "WIFI",
                                    getString(R.string.connected)
                                )
                            }
                            PrinterHelper.getInstance().getWirelessPrinterInfo(
                                WirelessPrintStyle.getWirelessPrintStyle()
                                    .setWirelessStyle(WirelessConfig.WIFI_IP),
                                object : IWirelessPrintResult.Stub() {
                                    override fun onResult(i: Int, s: String?) {}
                                    override fun onReturnString(s: String?) {
                                        Log.d(TAG,
                                            "ip回调==: s= $s"
                                        )
                                        activity?.runOnUiThread {
                                            LoadingDialogUtil.getInstance().hide()
                                            if (!s.isNullOrEmpty() && s != "-1") {
                                                baseIp = s
                                                binding.baseIPTv.visibility = View.VISIBLE
                                                binding.baseIPTv.text = String.format(
                                                    getString(R.string.status_ip),
                                                    s
                                                )
                                            } else {
                                                ToastUtil.showShort(
                                                    context,
                                                    R.string.connect_wifi_tips1
                                                )
                                            }
                                        }
                                    }
                                })
                        }
                    }
                }
            })
    }

    fun updatePrinterStatus(status: Int) {
        if (::binding.isInitialized) {
            if (status != 0) {
                baseIp = ""
                binding.baseIPTv.visibility = View.INVISIBLE
                binding.baseIPTv.text = ""
                binding.networkConfirmTv.isEnabled = false
                binding.networkConfirmTv.alpha = 0.5f
                binding.ipConfirmTv.isEnabled = false
                binding.ipConfirmTv.alpha = 0.5f
                binding.getConnectIPTv.isEnabled = false
                binding.getConnectIPTv.alpha = 0.5f
                LoadingDialogUtil.getInstance().hide()
            } else {
                binding.networkConfirmTv.isEnabled = true
                binding.networkConfirmTv.alpha = 1f
                binding.ipConfirmTv.isEnabled = true
                binding.ipConfirmTv.alpha = 1f
                binding.getConnectIPTv.isEnabled = true
                binding.getConnectIPTv.alpha = 1f
            }
        }
    }

    fun setCallback(listener: SwitchFragmentListener) {
        fragmentListener = listener
    }

    private fun switchFragment(num: Int) {
        fragmentListener?.switchFragment(num)
    }

    fun setSpinnerData(wifiList: ArrayList<String>) {
        this.list = wifiList
        if (list.isNotEmpty()) {
            binding.ssidSpinner.text = list[0]
        }
    }

    override fun onWifiListUpdated(list: ArrayList<String>) {
        activity?.runOnUiThread { setSpinnerData(list) }
    }

    override fun onPermissionRequired() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MainActivity.requestPermissionCode
        )
    }

    override fun onPermissionDenied() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onWifiDisabled() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Please enable WiFi", Toast.LENGTH_LONG).show()
        }
    }

    override fun onScanFailed() {}
    override fun onWifiConnectStatus(b: Boolean) {
        if (!b) {
            (activity as? MainActivity)?.disConnectWirelessPrint()
            disConnect()
        }
    }

    override fun onDestroyView() {
        wifiScanner.release()
        ExecutorServiceManager.shutdownExecutorService()
        super.onDestroyView()
    }

    private fun showPopup(context: Context, mList: List<String>, view: View) {
        popupWindow?.dismiss()
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_list, null)
        val listView = popupView.findViewById<ListView>(R.id.listView)
        listView.adapter = ListAdapter(context, mList)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            try {
                if (position < mList.size) {
                    binding.ssidSpinner.text = mList[position]
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
}