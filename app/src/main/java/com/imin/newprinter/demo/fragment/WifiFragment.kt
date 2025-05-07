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
import android.os.RemoteException
import android.os.SystemClock
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.feature.tui.util.ToastUtil
import com.imin.newprinter.demo.MainActivity
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.adapter.ListAdapter
import com.imin.newprinter.demo.bean.BluetoothDeviceInfo
import com.imin.newprinter.demo.callback.SwitchFragmentListener
import com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding
import com.imin.newprinter.demo.dialog.DistNetworkDialog
import com.imin.newprinter.demo.utils.BluetoothScanner
import com.imin.newprinter.demo.utils.ExecutorServiceManager
import com.imin.newprinter.demo.utils.LoadingDialogUtil
import com.imin.newprinter.demo.utils.NetworkValidator
import com.imin.newprinter.demo.utils.Utils
import com.imin.newprinter.demo.utils.WifiKeyName
import com.imin.newprinter.demo.utils.WifiScannerSingleton
import com.imin.newprinter.demo.view.DividerItemDecoration
import com.imin.newprinter.demo.view.OnSingleClickListener
import com.imin.printer.INeoPrinterCallback
import com.imin.printer.PrinterHelper

class WifiFragment : BaseFragment(), WifiScannerSingleton.WifiListListener,
    BluetoothScanner.BluetoothScanCallback {

    private val TAG = "PrintDemo_WifiFragment"
    private lateinit var binding: FragmentWifiConnectBinding
    private var list = ArrayList<String>()
    private var baseIp = ""
    private var isOpenEasy = false
    private var outoConnect = true
    private var isVisibleToView = false
    public lateinit var wifiScanner: WifiScannerSingleton
    private var checkWifi = 10
    private var retry = 10
    private var fragmentListener: SwitchFragmentListener? = null
    private var popupWindow: PopupWindow? = null
    private lateinit var adapterWifiConnect: BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>//已连接
    private lateinit var adapterBtConnect: BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>//已连接
    private lateinit var adapterUnConnect: BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>//未连接
    private lateinit var adapterUnWifi: BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>//没有配网
    private val connectedWifiList = java.util.ArrayList<BluetoothDeviceInfo>()
    private val connectedBtList = java.util.ArrayList<BluetoothDeviceInfo>()
    private val unConnectList = java.util.ArrayList<BluetoothDeviceInfo>()
    private val unWifiList = java.util.ArrayList<BluetoothDeviceInfo>()
    var networkDialog: DistNetworkDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWifiConnectBinding.inflate(inflater)
        wifiScanner = WifiScannerSingleton.getInstance(requireContext())
        initView()
        initData()
        initEvent()
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

        binding.rvWifiConnected.layoutManager = LinearLayoutManager(context)
        binding.rvBtConnected.layoutManager = LinearLayoutManager(context)
        binding.rvNotConnect.layoutManager = LinearLayoutManager(context)
        binding.rvWaitConnect.layoutManager = LinearLayoutManager(context)

        val dividerDrawable = context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
        if (dividerDrawable != null) {
            val itemDecoration = DividerItemDecoration(dividerDrawable)
            binding.rvWifiConnected.addItemDecoration(itemDecoration)
            binding.rvNotConnect.addItemDecoration(itemDecoration)
            binding.rvWaitConnect.addItemDecoration(itemDecoration)
            binding.rvBtConnected.addItemDecoration(itemDecoration)
        }

        binding.autoConnectIv.setImageResource(R.drawable.ic_check)
        binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close)
        binding.pwdEt.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.ipEt.isEnabled = false

//        0 ~ -30 信号强
//        -30 ~ -60 信号中
//        -60 ~ -99 信号弱
        //WiFi已连接
        adapterWifiConnect = object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
            override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
//                WiFi Status= 1, Printer Status= 0, IO Status= 1, IP Address= 192.171.1.6, rssiWifi= 56,
//                WiFi Name= iMer, rssiBle= -78 , device.name80mm Printer-LE ,device.address DD:0D:30:70:05:D9
                if (item != null){
                    holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                    holder.getView<TextView>(R.id.tvRvMac).text = item.address
                    holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                    holder.getView<TextView>(R.id.tvRvIp).text = item.ipAddress
                    holder.getView<TextView>(R.id.tvRvWifiName).text = item.wifiName
                    var tvRvBtConnect = holder.getView<TextView>(R.id.tvRvBtConnect)
                    //判断是否蓝牙连接如果有匹配 连接的mac地址，匹配不上之后显示按钮


                    var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                    when(getSignalStrength(item.rssiWifi)){
                        3->ivWifi.setImageResource(R.drawable.ic_wifi_3)
                        2->ivWifi.setImageResource(R.drawable.ic_wifi_2)
                        1->ivWifi.setImageResource(R.drawable.ic_wifi_1)
                        else->{
                            ivWifi.setImageResource(R.drawable.ic_wifi_0)
                        }
                    }
                    var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                    ivPrinterTag.setImageResource(if (item.printerWorkingStatus==1)
                        R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag)


                    var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                    var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)

                    if (getPrinterStatus(item.ioStatus)!=0){
                        tvPrinter.background = resources.getDrawable(R.drawable.dra_red_corner_5)
                        tvPrinter.isEnabled = false
                    }else{
                        tvPrinter.background = resources.getDrawable(R.drawable.dra_green_corner_5)
                        tvPrinter.isEnabled = true
                    }
                    tvDisconnectPrinter.setOnClickListener {

                    }


                }
            }
        }

        //蓝牙已连接
        adapterBtConnect = object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
            override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                if (item != null){
                    holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                    holder.getView<TextView>(R.id.tvRvMac).text = item.address
                    holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                    var tvIp = holder.getView<TextView>(R.id.tvRvIp)
                    var tvWifiName = holder.getView<TextView>(R.id.tvRvWifiName)
                    var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                    var tvRvWifiConnect = holder.getView<TextView>(R.id.tvRvWifiConnect)//如果WiFi没连上也显示

                    if (item.ipAddress.isNotEmpty()){
                        tvIp.text = item.ipAddress
                        tvWifiName.text = item.wifiName
                        tvIp.visibility = View.VISIBLE
                        tvWifiName.visibility = View.VISIBLE
                        ivWifi.visibility = View.VISIBLE
                        //如果WiFi没连接
                        tvRvWifiConnect.visibility = View.VISIBLE
                        tvRvWifiConnect.setOnClickListener {
                            //连接WiFi
                        }

                    }else{
                        tvIp.visibility = View.GONE
                        tvWifiName.visibility = View.GONE
                        ivWifi.visibility = View.GONE
                        tvRvWifiConnect.visibility = View.VISIBLE
                        tvRvWifiConnect.text = getString(R.string.distribution_network)
                        tvRvWifiConnect.setOnClickListener {
                            //去配网
                        }
                    }


                    when(getSignalStrength(item.rssiWifi)){
                        3->ivWifi.setImageResource(R.drawable.ic_wifi_3)
                        2->ivWifi.setImageResource(R.drawable.ic_wifi_2)
                        1->ivWifi.setImageResource(R.drawable.ic_wifi_1)
                        else->{
                            ivWifi.setImageResource(R.drawable.ic_wifi_0)
                        }
                    }
                    var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                    ivPrinterTag.setImageResource(if (item.printerWorkingStatus==1)
                        R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag)


                    var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                    var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)

                    if (getPrinterStatus(item.ioStatus)!=0){
                        tvPrinter.background = resources.getDrawable(R.drawable.dra_red_corner_5)
                        tvPrinter.isEnabled = false
                    }else{
                        tvPrinter.background = resources.getDrawable(R.drawable.dra_green_corner_5)
                        tvPrinter.isEnabled = true
                    }
                    tvDisconnectPrinter.setOnClickListener {

                    }


                }

            }
        }

        //待连接
        adapterUnConnect = object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
            override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                if (item != null){
                    holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                    holder.getView<TextView>(R.id.tvRvMac).text = item.address
                    holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                    holder.getView<TextView>(R.id.tvRvIp).text = item.ipAddress
                    holder.getView<TextView>(R.id.tvRvWifiName).text = item.wifiName
                    var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                    when(getSignalStrength(item.rssiWifi)){
                        3->ivWifi.setImageResource(R.drawable.ic_wifi_3)
                        2->ivWifi.setImageResource(R.drawable.ic_wifi_2)
                        1->ivWifi.setImageResource(R.drawable.ic_wifi_1)
                        else->{
                            ivWifi.setImageResource(R.drawable.ic_wifi_0)
                        }
                    }
                    var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                    ivPrinterTag.setImageResource(if (item.printerWorkingStatus==1)
                        R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag)


                    var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                    var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)
                    tvDisconnectPrinter.background = resources.getDrawable(R.drawable.dra_green_corner_5)
                    tvPrinter.text = getString(R.string.connect_bt)
                    tvDisconnectPrinter.text = getString(R.string.connect_wifi)

                    tvPrinter.setOnClickListener {
                        //连接WiFi 打印
                    }
                    tvDisconnectPrinter.setOnClickListener {
                        //连接wifi 打印

                    }


                }

            }
        }


        //待配网 待连接
        adapterUnWifi = object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
            override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                if (item != null){
                    holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                    holder.getView<TextView>(R.id.tvRvMac).text = item.address
                    holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                    holder.getView<TextView>(R.id.tvRvIp).visibility = View.GONE
                    holder.getView<TextView>(R.id.tvRvIp).visibility = View.GONE
                    holder.getView<TextView>(R.id.tvRvWifiName).visibility = View.GONE

                    var icon = holder.getView<ImageView>(R.id.ivRvIcon)
                    icon.setImageResource(R.drawable.ic_printer_unnormal)

                    var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                    ivPrinterTag.setImageResource( R.drawable.ic_printer_un_connect_tag)
                    var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                    ivWifi.visibility = View.GONE

                    var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                    var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)
                    tvDisconnectPrinter.background = resources.getDrawable(R.drawable.dra_green_corner_5)
                    tvPrinter.text = getString(R.string.connect_bt)
                    tvDisconnectPrinter.text = getString(R.string.distribution_network)

                    tvPrinter.setOnClickListener {
                        //蓝牙连接
                    }
                    tvDisconnectPrinter.setOnClickListener {
                        //WiFi配网
                        if (networkDialog != null){
                            networkDialog?.dismiss()
                            networkDialog = null;
                        }
                        networkDialog = DistNetworkDialog(context)
                        networkDialog?.setPopList(list)
                        networkDialog?.setMacAddress(item.address)

                        networkDialog!!.show()
                    }


                }

            }
        }



        binding.rvWifiConnected.adapter = adapterWifiConnect
        binding.rvNotConnect.adapter = adapterUnConnect
        binding.rvWaitConnect.adapter = adapterUnWifi
        binding.rvBtConnected.adapter = adapterBtConnect

        initTest()


    }

    private fun initTest() {
        adapterWifiConnect.replaceData(connectedWifiList)
        adapterBtConnect.replaceData(connectedBtList)
        adapterUnConnect.replaceData(unConnectList)
        adapterUnWifi.replaceData(unWifiList)
    }

    private fun initEvent() {
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

                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE,
                    "USB",
                    object : INeoPrinterCallback() {
                        @Throws(RemoteException::class)
                        override fun onRunResult(b: Boolean) {
                        }

                        @Throws(RemoteException::class)
                        override fun onReturnString(s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onRaiseException(i: Int, s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onPrintResult(i: Int, s: String) {
                        }
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
                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE,
                    "USB",
                    object : INeoPrinterCallback() {
                        @Throws(RemoteException::class)
                        override fun onRunResult(b: Boolean) {
                        }

                        @Throws(RemoteException::class)
                        override fun onReturnString(s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onRaiseException(i: Int, s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onPrintResult(i: Int, s: String) {
                        }
                    })
                if (!outoConnect) {
                    val ip = binding.ipEt.text.toString().trim()
                    if (ip.isEmpty() || !NetworkValidator.validateIP(ip)) {
                        Toast.makeText(context, getText(R.string.ip_tips), Toast.LENGTH_SHORT)
                            .show()
                        LoadingDialogUtil.getInstance().hide()
                        return
                    }

                    PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_CONNECT_IP,
                        ip,
                        object : INeoPrinterCallback() {
                            @Throws(RemoteException::class)
                            override fun onRunResult(b: Boolean) {
                            }

                            @Throws(RemoteException::class)
                            override fun onReturnString(s: String) {
                            }

                            @Throws(RemoteException::class)
                            override fun onRaiseException(i: Int, s: String) {
                            }

                            @Throws(RemoteException::class)
                            override fun onPrintResult(i: Int, s: String) {
                                Log.d(TAG, "WIFI_CONNECT => $s")
                                activity?.runOnUiThread {
                                    LoadingDialogUtil.getInstance().hide()
                                    if (i == 1) {
                                        MainActivity.connectAddress = s
                                        MainActivity.ipConnect = s
                                        MainActivity.connectType = "WIFI"
                                        MainActivity.connectContent =
                                            binding.wifiIPTv.text.toString().trim()
                                        switchFragment(4)

                                        PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE,
                                            "WIFI",
                                            object : INeoPrinterCallback() {
                                                @Throws(RemoteException::class)
                                                override fun onRunResult(b: Boolean) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onReturnString(s: String) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onRaiseException(i: Int, s: String) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onPrintResult(i: Int, s: String) {
                                                }
                                            })

                                    } else {
                                        ToastUtil.showShort(context, R.string.connect_fail)
                                    }
                                }
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

                    PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_CONNECT_IP,
                        MainActivity.ipConnect,
                        object : INeoPrinterCallback() {
                            @Throws(RemoteException::class)
                            override fun onRunResult(b: Boolean) {
                            }

                            @Throws(RemoteException::class)
                            override fun onReturnString(s: String) {
                            }

                            @Throws(RemoteException::class)
                            override fun onRaiseException(i: Int, s: String) {
                            }

                            @Throws(RemoteException::class)
                            override fun onPrintResult(i: Int, s: String) {
                                activity?.runOnUiThread {
                                    LoadingDialogUtil.getInstance().hide()
                                    if (i == 1) {
                                        baseIp = ""
                                        binding.baseIPTv.text = ""
                                        binding.baseIPTv.visibility = View.INVISIBLE
                                        MainActivity.connectAddress = s
                                        MainActivity.ipConnect = s
                                        MainActivity.connectType = "WIFI"
                                        MainActivity.connectContent =
                                            binding.wifiIPTv.text.toString().trim()
                                        switchFragment(4)
                                        PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE,
                                            "WIFI",
                                            object : INeoPrinterCallback() {
                                                @Throws(RemoteException::class)
                                                override fun onRunResult(b: Boolean) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onReturnString(s: String) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onRaiseException(i: Int, s: String) {
                                                }

                                                @Throws(RemoteException::class)
                                                override fun onPrintResult(i: Int, s: String) {
                                                }
                                            })
                                    } else {
                                        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        })

                }
            }
        })

        binding.getConnectIPTv.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                LoadingDialogUtil.getInstance()
                    .show(requireContext(), getString(R.string.toast_tips1))

                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE,
                    "USB",
                    object : INeoPrinterCallback() {
                        @Throws(RemoteException::class)
                        override fun onRunResult(b: Boolean) {
                        }

                        @Throws(RemoteException::class)
                        override fun onReturnString(s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onRaiseException(i: Int, s: String) {
                        }

                        @Throws(RemoteException::class)
                        override fun onPrintResult(i: Int, s: String) {
                        }
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

        PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_DHCP,
            if (binding.swichStatic.isChecked) "IpType" else "STATIC",
            object : INeoPrinterCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(b: Boolean) {
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }

                @Throws(RemoteException::class)
                override fun onRaiseException(i: Int, s: String) {
                }

                @Throws(RemoteException::class)
                override fun onPrintResult(i: Int, s: String) {
                    Log.d(TAG, "WIFI_IP_TYPE =>$s  i=$i")
                    activity!!.runOnUiThread {
                        if (i < 1) {
                            LoadingDialogUtil.getInstance().hide()
                            ToastUtil.showShort(context, R.string.connect_fail)
                        } else {
                            val list1: MutableList<String> =
                                java.util.ArrayList()
                            list1.add("USB")
                            list1.add(ssid)
                            list1.add(pwd)
                            PrinterHelper.getInstance().setPrinterAction(
                                WifiKeyName.WIFI_SETUP_NET,
                                list1,
                                object : INeoPrinterCallback() {
                                    @Throws(RemoteException::class)
                                    override fun onRunResult(b: Boolean) {
                                    }

                                    @Throws(RemoteException::class)
                                    override fun onReturnString(s: String) {
                                    }

                                    @Throws(RemoteException::class)
                                    override fun onRaiseException(i: Int, s: String) {
                                    }

                                    @Throws(RemoteException::class)
                                    override fun onPrintResult(i: Int, s: String) {
                                        Log.d(TAG, "配网回调==: i= $i ,s=>$s")
                                        if (i == 1) {
                                            activity!!.runOnUiThread {
                                                binding.wifiStatusTv.text = String.format(
                                                    getString(R.string.status_wifi),
                                                    "WIFI",
                                                    getString(R.string.connected)
                                                )
                                            }
                                            retry = 10
                                            sendSsid()
                                        } else {
                                            //失败
                                            activity!!.runOnUiThread {
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
                                })
                        }
                    }
                }
            })
    }

    private fun sendSsid() {
        Log.d(TAG, "retry=> $retry")

        PrinterHelper.getInstance()
            .getPrinterInfo(WifiKeyName.WIRELESS_CONNECT_STATUS, object : INeoPrinterCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(b: Boolean) {
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                    Log.d(TAG, "WIFI_CONNECT_STATUS==: s= $s")
                    if (!Utils.isEmpty(s)) {
                        if (s == "0" || s == "2") {
                            retry--
                            if (retry > 0) {
                                SystemClock.sleep(1000)
                                sendSsid()
                            } else {
                                activity!!.runOnUiThread {
                                    binding.wifiStatusTv.text = String.format(
                                        getString(R.string.status_wifi),
                                        "WIFI",
                                        getString(R.string.un_connected)
                                    )
                                    binding.wifiIPTv.text =
                                        String.format(getString(R.string.status_ip), "------")
                                    binding.connectNetworkTv.background =
                                        context!!.resources.getDrawable(R.drawable.dra_green60_corner_5)
                                    binding.connectTv.background =
                                        context!!.resources.getDrawable(R.drawable.dra_gray_corner_5)
                                    binding.clConnectNetwork.visibility = View.VISIBLE
                                    binding.clConnectIP.visibility = View.INVISIBLE
                                    LoadingDialogUtil.getInstance().hide()
                                }
                                retry = 10
                            }
                        } else if (s == "-1") {
                            checkWifi = 10
                            activity!!.runOnUiThread {
                                LoadingDialogUtil.getInstance().hide()
                                ToastUtil.showShort(context, R.string.get_fail)
                            }
                        } else {
                            retry = 10
                            activity!!.runOnUiThread {
                                binding.wifiStatusTv.text =
                                    String.format(
                                        getString(R.string.status_wifi),
                                        "WIFI",
                                        getString(R.string.connected)
                                    )
                            }
                            PrinterHelper.getInstance().getPrinterInfo(
                                WifiKeyName.WIFI_IP,
                                object : INeoPrinterCallback() {
                                    @Throws(RemoteException::class)
                                    override fun onRunResult(b: Boolean) {
                                    }

                                    @Throws(RemoteException::class)
                                    override fun onReturnString(s: String) {
                                        Log.d(TAG, "ip回调==: s= $s")
                                        activity!!.runOnUiThread {
                                            if (!Utils.isEmpty(s) && s != "-1") {
                                                binding.baseIPTv.visibility = View.INVISIBLE
                                                baseIp = ""
                                                binding.baseIPTv.text = ""
                                                MainActivity.ipConnect = s
                                                MainActivity.connectAddress = s
                                                binding.connectTv.background =
                                                    context!!.resources.getDrawable(R.drawable.dra_green60_corner_5)
                                                binding.connectNetworkTv.background =
                                                    context!!.resources.getDrawable(R.drawable.dra_gray_corner_5)
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

                                    @Throws(RemoteException::class)
                                    override fun onRaiseException(i: Int, s: String) {
                                    }

                                    @Throws(RemoteException::class)
                                    override fun onPrintResult(i: Int, s: String) {
                                    }
                                })
                        }
                    }
                }

                @Throws(RemoteException::class)
                override fun onRaiseException(i: Int, s: String) {
                }

                @Throws(RemoteException::class)
                override fun onPrintResult(i: Int, s: String) {
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
                Log.e(TAG, "startLeScanWithCoroutines 开始}")
                startBleScan()

            } catch (e: Exception) {
                Log.e(TAG, "Error starting wifi scan: ${e.message}")
            }
        }

        if (MainActivity.ipConnect.isEmpty()) {

            //判断SDK 是否有连接
            PrinterHelper.getInstance().getPrinterInfo(
                WifiKeyName.WIFI_CURRENT_CONNECT_IP,
                object : INeoPrinterCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(b: Boolean) {
                    }

                    @Throws(RemoteException::class)
                    override fun onReturnString(s: String) {
                        Log.d(TAG,
                            "initData==CURRENT_CONNECT_WIFI_IP: $s  "
                        )
                        activity?.runOnUiThread {
                            if (!Utils.isEmpty(s)) {
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

                    @Throws(RemoteException::class)
                    override fun onRaiseException(i: Int, s: String) {
                    }

                    @Throws(RemoteException::class)
                    override fun onPrintResult(i: Int, s: String) {
                    }
                })
        } else {
            updateUi()
        }

//        updatePrinterStatus(PrinterHelper.getInstance().printerStatus)
    }

    public fun startBleScan() {
        BluetoothScanner.startLeScanWithCoroutines(this,this)
    }

    private fun disConnect() {
        PrinterHelper.getInstance()
            .setPrinterAction(WifiKeyName.WIFI_DISCONNECT, MainActivity.ipConnect, object : INeoPrinterCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(b: Boolean) {
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                }

                @Throws(RemoteException::class)
                override fun onRaiseException(i: Int, s: String) {
                }

                @Throws(RemoteException::class)
                override fun onPrintResult(i: Int, s: String) {
                    Log.d(TAG, "DISCONNECT_WIFI==:  $s")
                    activity!!.runOnUiThread {
                        binding.wifiStatusTv.text =
                            String.format(
                                getString(R.string.status_wifi),
                                "WIFI",
                                getString(R.string.un_connected)
                            )
                        binding.wifiIPTv.text =
                            String.format(getString(R.string.status_ip), "-------")
                        MainActivity.ipConnect = ""
                    }
                }
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

        PrinterHelper.getInstance()
            .getPrinterInfo(WifiKeyName.WIRELESS_CONNECT_STATUS, object : INeoPrinterCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(b: Boolean) {
                }

                @Throws(RemoteException::class)
                override fun onReturnString(s: String) {
                    Log.d(TAG, "WIFI_CONNECT_STATUS==: s= $s") //1已连接  0是没连接
                    if (!Utils.isEmpty(s)) {
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

                                PrinterHelper.getInstance().getPrinterInfo(
                                    WifiKeyName.WIFI_IP,
                                    object : INeoPrinterCallback() {
                                        @Throws(RemoteException::class)
                                        override fun onRunResult(b: Boolean) {
                                        }

                                        @Throws(RemoteException::class)
                                        override fun onReturnString(s: String) {
                                            Log.d(TAG, "ip回调==: s= $s")
                                            activity!!.runOnUiThread {
                                                LoadingDialogUtil.getInstance().hide()
                                                if (!Utils.isEmpty(s) && s != "-1") {
                                                    baseIp = s
                                                    binding.baseIPTv.visibility = View.VISIBLE
                                                    binding.baseIPTv.text =
                                                        String.format(getString(R.string.status_ip), s)
                                                } else {
                                                    ToastUtil.showShort(
                                                        context,
                                                        R.string.connect_wifi_tips1
                                                    )
                                                }
                                            }
                                        }

                                        @Throws(RemoteException::class)
                                        override fun onRaiseException(i: Int, s: String) {
                                        }

                                        @Throws(RemoteException::class)
                                        override fun onPrintResult(i: Int, s: String) {
                                        }
                                    })
                                
                                
                            }
                        }
                        
                    } else {
                        checkWifi = 10
                        activity!!.runOnUiThread {
                            ToastUtil.showShort(context, R.string.get_fail)
                            LoadingDialogUtil.getInstance().hide()
                        }
                    }
                }

                @Throws(RemoteException::class)
                override fun onRaiseException(i: Int, s: String) {
                }

                @Throws(RemoteException::class)
                override fun onPrintResult(i: Int, s: String) {
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
        BluetoothScanner.stopLeScanCompletely()
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

    override fun onDeviceFound(deviceAddress: String, deviceName: String) {

    }

    override fun onManufacturerDataParsed(deviceInfo: BluetoothDeviceInfo) {
        if (deviceInfo != null){
            when (deviceInfo.wifiConnectStatus){
                1->{//已经配网
                    if (unConnectList.isNotEmpty() && unConnectList.size>0){
                        // 查找匹配的设备索引
                        val index = unConnectList.indexOfFirst { it.address == deviceInfo.address }

                        if (index != -1) {
                            // 替换旧设备信息为新设备信息
                            unConnectList[index] = deviceInfo
                        } else {
                            println("No matching device found")
                            unConnectList.add(deviceInfo)
                        }
                    }else{
                        unConnectList.add(deviceInfo)
                    }
                    adapterUnConnect.replaceData(unConnectList)

                    if (unWifiList.isNotEmpty() && unWifiList.size>0){
                        val index = unWifiList.indexOfFirst { it.address == deviceInfo.address }
                        if (index != -1) {
                            unWifiList.removeAt(index)
                        }
                        adapterUnWifi.replaceData(unWifiList)
                    }


                }
                0->{//没有配网
                    if (unWifiList.isNotEmpty() && unWifiList.size>0){
                        // 查找匹配的设备索引
                        val index = unWifiList.indexOfFirst { it.address == deviceInfo.address }

                        if (index != -1) {
                            // 替换旧设备信息为新设备信息
                            unWifiList[index] = deviceInfo
                        } else {
                            println("No matching device found")
                            unWifiList.add(deviceInfo)
                        }
                    }else{
                        unWifiList.add(deviceInfo)
                    }

                    adapterUnWifi.replaceData(unWifiList)
                }
            }

            adapterWifiConnect.replaceData(connectedWifiList)
            adapterBtConnect.replaceData(connectedBtList)


        }

    }

    fun getSignalStrength(rssi: Int): Int {
        return when (rssi) {
            in -30..0 -> 3   // 信号强（优化点：使用自然区间表达式）
            in -60 until -30 -> 2 // 信号中（优化点：明确开闭区间）
            in -99 until -60 -> 1 // 信号弱
            else -> 0             // 无效信号（覆盖极端值）
        }
    }

    fun getPrinterStatus(status: String): Int {
        return when (status) {
            "000" -> 0//"正常"
            "001" -> 7// "缺纸"
            "010" -> 5//"过热"
            "011" -> 3//"开盖"
            "110" -> 6//"卡切刀"
            else -> {
                -1
            }
        }
    }

}