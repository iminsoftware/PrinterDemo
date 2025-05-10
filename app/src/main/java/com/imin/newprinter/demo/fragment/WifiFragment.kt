package com.imin.newprinter.demo.fragment

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.imin.newprinter.demo.MainActivity
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.bean.BluetoothDeviceInfo
import com.imin.newprinter.demo.callback.SwitchFragmentListener
import com.imin.newprinter.demo.databinding.FragmentWifiBinding
import com.imin.newprinter.demo.dialog.DistNetworkDialog
import com.imin.newprinter.demo.utils.BluetoothScanner
import com.imin.newprinter.demo.utils.ExecutorServiceManager
import com.imin.newprinter.demo.utils.LoadingDialogUtil
import com.imin.newprinter.demo.utils.MdnsDiscovery
import com.imin.newprinter.demo.utils.NetworkServiceHelper
import com.imin.newprinter.demo.utils.Utils
import com.imin.newprinter.demo.utils.WifiKeyName
import com.imin.newprinter.demo.utils.WifiScannerSingleton
import com.imin.newprinter.demo.view.DividerItemDecoration
import com.imin.printer.INeoPrinterCallback
import com.imin.printer.PrinterHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener

class WifiFragment : BaseFragment(), WifiScannerSingleton.WifiListListener,
    BluetoothScanner.BluetoothScanCallback {

    private val TAG = "PrintDemo_WifiFragment"
    private lateinit var binding: FragmentWifiBinding
    private var list = ArrayList<String>()
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
    private var connectedWifiList = java.util.ArrayList<BluetoothDeviceInfo>()
    private var connectedBtList = java.util.ArrayList<BluetoothDeviceInfo>()
    private var unConnectList = java.util.ArrayList<BluetoothDeviceInfo>()
    private var unWifiList = java.util.ArrayList<BluetoothDeviceInfo>()
    private var ipConnectList: List<String>? =null
    var networkDialog: DistNetworkDialog? = null
    private lateinit var serviceHelper: NetworkServiceHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWifiBinding.inflate(inflater)
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

        // 设置发现回调（必须在主线程）
        serviceHelper = context?.let { NetworkServiceHelper(it) }!!



        binding.rvWifiConnected.layoutManager = LinearLayoutManager(context)
        binding.rvBtConnected.layoutManager = LinearLayoutManager(context)
        binding.rvNotConnect.layoutManager = LinearLayoutManager(context)
        binding.rvWaitConnect.layoutManager = LinearLayoutManager(context)

        val dividerDrawable =
            context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
        if (dividerDrawable != null) {
            val itemDecoration = DividerItemDecoration(dividerDrawable)
            binding.rvWifiConnected.addItemDecoration(itemDecoration)
            binding.rvNotConnect.addItemDecoration(itemDecoration)
            binding.rvWaitConnect.addItemDecoration(itemDecoration)
            binding.rvBtConnected.addItemDecoration(itemDecoration)
        }


//        0 ~ -30 信号强
//        -30 ~ -60 信号中
//        -60 ~ -99 信号弱
        //WiFi已连接
        adapterWifiConnect =
            object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
                override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                    if (item != null) {
                        holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                        holder.getView<TextView>(R.id.tvRvMac).text = item.address
                        holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                        holder.getView<TextView>(R.id.tvRvIp).text = item.ipAddress
                        holder.getView<TextView>(R.id.tvRvWifiName).text = item.wifiName
                        var tvRvBtConnect = holder.getView<TextView>(R.id.tvRvBtConnect)
                        //判断是否蓝牙连接如果有匹配 连接的mac地址，匹配不上之后显示按钮


                        var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                        when (getSignalStrength(item.rssiWifi)) {
                            3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                            2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                            1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                            else -> {
                                ivWifi.setImageResource(R.drawable.ic_wifi_0)
                            }
                        }
                        var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                        ivPrinterTag.setImageResource(
                            if (item.printerWorkingStatus == 1)
                                R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag
                        )


                        var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                        var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)

                        if (getPrinterStatus(item.ioStatus) != 0) {
                            tvPrinter.background =
                                resources.getDrawable(R.drawable.dra_red_corner_5)
                            tvPrinter.isEnabled = false
                        } else {
                            tvPrinter.background =
                                resources.getDrawable(R.drawable.dra_green_corner_5)
                            tvPrinter.isEnabled = true
                        }
                        tvPrinter.setOnClickListener {

                        }
                        tvDisconnectPrinter.setOnClickListener {

                        }


                    }
                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder,
                    position: Int,
                    payloads: MutableList<Any>
                ) {
                    if (payloads.isNotEmpty()) {
                        for (payload in payloads) {
                            if (payload is BluetoothDeviceInfo) {
                                // 局部更新逻辑
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()

                                val ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                                when (getSignalStrength(payload.rssiWifi)) {
                                    3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                                    2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                                    1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                                    else -> ivWifi.setImageResource(R.drawable.ic_wifi_0)
                                }

                                val ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                                ivPrinterTag.setImageResource(
                                    if (payload.printerWorkingStatus == 1) R.drawable.ic_printer_busy_tag
                                    else R.drawable.ic_printer_normal_tag
                                )

                                var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                                if (getPrinterStatus(payload.ioStatus) != 0) {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_red_corner_5)
                                    tvPrinter.isEnabled = false
                                } else {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_green_corner_5)
                                    tvPrinter.isEnabled = true
                                }
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()

                                holder.getView<TextView>(R.id.tvRvTitle).text = payload.name
                                holder.getView<TextView>(R.id.tvRvIp).text = payload.ipAddress
                                holder.getView<TextView>(R.id.tvRvWifiName).text = payload.wifiName
                            }

                        }
                    } else {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }

        //蓝牙已连接
        adapterBtConnect =
            object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
                override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                    if (item != null) {
                        holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                        holder.getView<TextView>(R.id.tvRvMac).text = item.address
                        holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                        var tvIp = holder.getView<TextView>(R.id.tvRvIp)
                        var tvWifiName = holder.getView<TextView>(R.id.tvRvWifiName)
                        var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                        var tvRvWifiConnect =
                            holder.getView<TextView>(R.id.tvRvWifiConnect)//如果WiFi没连上也显示

                        if (item.ipAddress.isNotEmpty()) {
                            tvIp.text = item.ipAddress
                            tvWifiName.text = item.wifiName
                            tvIp.visibility = View.VISIBLE
                            tvWifiName.visibility = View.VISIBLE
                            ivWifi.visibility = View.VISIBLE
                            //如果WiFi没连接
                            tvRvWifiConnect.visibility = View.VISIBLE
                            tvRvWifiConnect.text = getString(R.string.connect_wifi)
                            tvRvWifiConnect.setOnClickListener {
                                //连接WiFi
                            }

                        } else {
                            tvIp.visibility = View.GONE
                            tvWifiName.visibility = View.GONE
                            ivWifi.visibility = View.GONE
                            tvRvWifiConnect.visibility = View.VISIBLE
                            tvRvWifiConnect.text = getString(R.string.distribution_network)
                            tvRvWifiConnect.setOnClickListener {
                                //去配网
                            }
                        }


                        when (getSignalStrength(item.rssiWifi)) {
                            3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                            2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                            1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                            else -> {
                                ivWifi.setImageResource(R.drawable.ic_wifi_0)
                            }
                        }
                        var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                        ivPrinterTag.setImageResource(
                            if (item.printerWorkingStatus == 1)
                                R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag
                        )


                        var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                        var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)

                        if (getPrinterStatus(item.ioStatus) != 0) {
                            tvPrinter.background =
                                resources.getDrawable(R.drawable.dra_red_corner_5)
                            tvPrinter.isEnabled = false
                        } else {
                            tvPrinter.background =
                                resources.getDrawable(R.drawable.dra_green_corner_5)
                            tvPrinter.isEnabled = true
                        }

                        tvPrinter.setOnClickListener {
                            PrinterHelper.getInstance().setPrinterAction(
                                WifiKeyName.WIRELESS_CONNECT_TYPE,
                                "BT",
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
                                        Log.d(TAG, "WIRELESS_CONNECT_TYPE  BT =>$s  i=$i")
                                        activity!!.runOnUiThread {
                                            if (i == 1) {
                                                MainActivity.btContent =
                                                    item.address
                                                MainActivity.connectType = "BT"
                                                MainActivity.connectContent =
                                                    item.name + "\t" + item.address
                                                switchFragment(4)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    getText(R.string.connect_fail),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                })
                        }
                        tvDisconnectPrinter.setOnClickListener {
                            //断开蓝牙连接
                            Log.d(TAG, "断开蓝牙连接")
                            disConnectBt()
                        }


                    }

                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder,
                    position: Int,
                    payloads: MutableList<Any>
                ) {
                    if (payloads.isNotEmpty()) {
                        for (payload in payloads) {
                            if (payload is BluetoothDeviceInfo) {
                                // 局部更新逻辑
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()

                                val ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                                when (getSignalStrength(payload.rssiWifi)) {
                                    3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                                    2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                                    1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                                    else -> ivWifi.setImageResource(R.drawable.ic_wifi_0)
                                }

                                val ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                                ivPrinterTag.setImageResource(
                                    if (payload.printerWorkingStatus == 1) R.drawable.ic_printer_busy_tag
                                    else R.drawable.ic_printer_normal_tag
                                )

                                var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                                if (getPrinterStatus(payload.ioStatus) != 0) {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_red_corner_5)
                                    tvPrinter.isEnabled = false
                                } else {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_green_corner_5)
                                    tvPrinter.isEnabled = true
                                }
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()
                                holder.getView<TextView>(R.id.tvRvTitle).text = payload.name
                                holder.getView<TextView>(R.id.tvRvIp).text = payload.ipAddress
                                holder.getView<TextView>(R.id.tvRvWifiName).text = payload.wifiName
                            }

                        }
                    } else {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }

        //待连接
        adapterUnConnect =
            object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
                override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                    if (item != null) {
                        holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                        holder.getView<TextView>(R.id.tvRvMac).text = item.address
                        holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                        holder.getView<TextView>(R.id.tvRvIp).text = item.ipAddress
                        holder.getView<TextView>(R.id.tvRvWifiName).text = item.wifiName
                        var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)

                        var tvRvWifiConnect = holder.getView<TextView>(R.id.tvRvWifiConnect)
                        tvRvWifiConnect.text = getString(R.string.distribution_network)
                        tvRvWifiConnect.visibility = View.VISIBLE

                        when (getSignalStrength(item.rssiWifi)) {
                            3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                            2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                            1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                            else -> {
                                ivWifi.setImageResource(R.drawable.ic_wifi_0)
                            }
                        }
                        var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                        ivPrinterTag.setImageResource(
                            if (item.printerWorkingStatus == 1)
                                R.drawable.ic_printer_busy_tag else R.drawable.ic_printer_normal_tag
                        )


                        var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                        var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)
                        tvDisconnectPrinter.background =
                            resources.getDrawable(R.drawable.dra_green_corner_5)
                        tvPrinter.text = getString(R.string.connect_wifi)
                        tvDisconnectPrinter.text = getString(R.string.connect_bt)

                        tvPrinter.setOnClickListener {
                            //连接WiFi 打印
                           // disConnectWifi()

                            PrinterHelper.getInstance().setPrinterAction(
                                WifiKeyName.WIRELESS_CONNECT_TYPE,
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
                                        Log.d(TAG, "WIRELESS_CONNECT_TYPE  WIFI =>$s  i=$i")
                                        if (i == 1) {
                                            PrinterHelper.getInstance().setPrinterAction(
                                                WifiKeyName.WIFI_CONNECT_IP,
                                                item.ipAddress,
                                                object : INeoPrinterCallback() {
                                                    @Throws(RemoteException::class)
                                                    override fun onRunResult(b: Boolean) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onReturnString(s: String) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onRaiseException(
                                                        i: Int,
                                                        s: String
                                                    ) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onPrintResult(i: Int, s: String) {
                                                        Log.d(TAG, "WIFI_CONNECT=>$s  i=$i")
                                                        activity!!.runOnUiThread {
                                                            if (i == 1) {
                                                                MainActivity.ipConnect =
                                                                    item.ipAddress
                                                                MainActivity.connectType = "WIFI"
                                                                MainActivity.connectContent =
                                                                    item.ipAddress
                                                                switchFragment(4)
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    getText(R.string.connect_fail),
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                            LoadingDialogUtil.getInstance().hide()
                                                        }
                                                    }
                                                })
                                        }
                                    }
                                })

                        }
                        tvDisconnectPrinter.setOnClickListener {
                            //连接 蓝牙 打印
                            disConnectBt()

                            PrinterHelper.getInstance().setPrinterAction(
                                WifiKeyName.WIRELESS_CONNECT_TYPE,
                                "BT",
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
                                        Log.d(TAG, "WIRELESS_CONNECT_TYPE  BT =>$s  i=$i")
                                        if (i == 1) {
                                            PrinterHelper.getInstance().setPrinterAction(
                                                WifiKeyName.BT_CONNECT_MAC,
                                                item.address,
                                                object : INeoPrinterCallback() {
                                                    @Throws(RemoteException::class)
                                                    override fun onRunResult(b: Boolean) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onReturnString(s: String) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onRaiseException(
                                                        i: Int,
                                                        s: String
                                                    ) {
                                                    }

                                                    @Throws(RemoteException::class)
                                                    override fun onPrintResult(i: Int, s: String) {
                                                        Log.d(TAG, "BT_CONNECT_MAC=>$s  i=$i")
                                                        activity!!.runOnUiThread {
                                                            if (i == 1) {
                                                                MainActivity.btContent =
                                                                    item.address
                                                                MainActivity.connectType = "BT"
                                                                MainActivity.connectContent =
                                                                    item.name + "\t" + item.address
                                                                switchFragment(4)

                                                                if (connectedBtList.isEmpty()) {
                                                                    connectedBtList.add(item)
                                                                    adapterBtConnect.replaceData(connectedBtList)
                                                                } else {
                                                                    connectedBtList.fill(item)
                                                                    adapterBtConnect.notifyItemChanged(0, item)
                                                                }

                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    getText(R.string.connect_fail),
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                            LoadingDialogUtil.getInstance().hide()
                                                        }
                                                    }
                                                })
                                        }
                                    }
                                })


                        }
                        tvRvWifiConnect.setOnClickListener {
                            if (networkDialog != null) {
                                networkDialog?.dismiss()
                                networkDialog = null;
                            }
                            networkDialog = DistNetworkDialog(context).apply {
                                setPopList(list)
                                setMacAddress(item.address)
                            }

                            networkDialog!!.show()
                        }
                    }

                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder,
                    position: Int,
                    payloads: MutableList<Any>
                ) {
                    if (payloads.isNotEmpty()) {
                        for (payload in payloads) {
                            if (payload is BluetoothDeviceInfo) {
                                // 局部更新逻辑
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()

                                val ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                                when (getSignalStrength(payload.rssiWifi)) {
                                    3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                                    2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                                    1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                                    else -> ivWifi.setImageResource(R.drawable.ic_wifi_0)
                                }

                                val ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                                ivPrinterTag.setImageResource(
                                    if (payload.printerWorkingStatus == 1) R.drawable.ic_printer_busy_tag
                                    else R.drawable.ic_printer_normal_tag
                                )

                                var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                                if (getPrinterStatus(payload.ioStatus) != 0) {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_red_corner_5)
                                    tvPrinter.isEnabled = false
                                } else {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_green_corner_5)
                                    tvPrinter.isEnabled = true
                                }
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()
                                holder.getView<TextView>(R.id.tvRvTitle).text = payload.name
                                holder.getView<TextView>(R.id.tvRvIp).text = payload.ipAddress
                                holder.getView<TextView>(R.id.tvRvWifiName).text = payload.wifiName
                            }

                        }
                    } else {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }


        //待配网 待连接
        adapterUnWifi =
            object : BaseQuickAdapter<BluetoothDeviceInfo, BaseViewHolder>(R.layout.item_wifi_rv) {
                override fun convert(holder: BaseViewHolder, item: BluetoothDeviceInfo) {
                    if (item != null) {
                        holder.getView<TextView>(R.id.tvRvTitle).text = item.name
                        holder.getView<TextView>(R.id.tvRvMac).text = item.address
                        holder.getView<TextView>(R.id.tvRvBtRssi).text = item.rssiBle.toString()
                        holder.getView<TextView>(R.id.tvRvIp).visibility = View.GONE
                        holder.getView<TextView>(R.id.tvRvWifiName).visibility = View.GONE

                        var icon = holder.getView<ImageView>(R.id.ivRvIcon)
                        icon.setImageResource(R.drawable.ic_printer_unnormal)

                        var ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                        ivPrinterTag.setImageResource(R.drawable.ic_printer_un_connect_tag)
                        var ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                        ivWifi.visibility = View.GONE

                        var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                        var tvDisconnectPrinter = holder.getView<TextView>(R.id.tvRvConnect)
                        tvDisconnectPrinter.background =
                            resources.getDrawable(R.drawable.dra_green_corner_5)
                        tvPrinter.text = getString(R.string.connect_bt)
                        tvDisconnectPrinter.text = getString(R.string.distribution_network)

                        tvPrinter.setOnClickListener {
                            //蓝牙连接
                        }
                        tvDisconnectPrinter.setOnClickListener {
                            //WiFi配网
                            if (networkDialog != null) {
                                networkDialog?.dismiss()
                                networkDialog = null;
                            }
                            networkDialog = DistNetworkDialog(context).apply {
                                setPopList(list)
                                setMacAddress(item.address)
                            }

                            networkDialog!!.show()
                        }


                    }

                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder,
                    position: Int,
                    payloads: MutableList<Any>
                ) {
                    if (payloads.isNotEmpty()) {
                        for (payload in payloads) {
                            if (payload is BluetoothDeviceInfo) {
                                // 局部更新逻辑
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()

                                val ivWifi = holder.getView<ImageView>(R.id.ivRvWifi)
                                when (getSignalStrength(payload.rssiWifi)) {
                                    3 -> ivWifi.setImageResource(R.drawable.ic_wifi_3)
                                    2 -> ivWifi.setImageResource(R.drawable.ic_wifi_2)
                                    1 -> ivWifi.setImageResource(R.drawable.ic_wifi_1)
                                    else -> ivWifi.setImageResource(R.drawable.ic_wifi_0)
                                }

                                val ivPrinterTag = holder.getView<ImageView>(R.id.ivRvTag)
                                ivPrinterTag.setImageResource(
                                    if (payload.printerWorkingStatus == 1) R.drawable.ic_printer_busy_tag
                                    else R.drawable.ic_printer_normal_tag
                                )

                                var tvPrinter = holder.getView<TextView>(R.id.tvRvPrint)
                                if (getPrinterStatus(payload.ioStatus) != 0) {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_red_corner_5)
                                    tvPrinter.isEnabled = false
                                } else {
                                    tvPrinter.background =
                                        resources.getDrawable(R.drawable.dra_green_corner_5)
                                    tvPrinter.isEnabled = true
                                }
                                holder.getView<TextView>(R.id.tvRvBtRssi).text =
                                    payload.rssiBle.toString()
                                holder.getView<TextView>(R.id.tvRvTitle).text = payload.name

                            }

                        }
                    } else {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }

        binding.rvWifiConnected.adapter = adapterWifiConnect
        binding.rvNotConnect.adapter = adapterUnConnect
        binding.rvWaitConnect.adapter = adapterUnWifi
        binding.rvBtConnected.adapter = adapterBtConnect

        adapterWifiConnect.replaceData(connectedWifiList)
        adapterBtConnect.replaceData(connectedBtList)
        adapterUnConnect.replaceData(unConnectList)
        adapterUnWifi.replaceData(unWifiList)

    }

    private fun initEvent() {

    }

    private fun initData() {
        // 使用 lifecycleScope 确保生命周期安全
        lifecycleScope.launchWhenResumed {
            try {
                Log.e(TAG, "startWifiScan 开始}")
                startWifiScan()
                Log.e(TAG, "startLeScanWithCoroutines 开始}")
                startBleScan()
                startDnsScan()

            } catch (e: Exception) {
                Log.e(TAG, "Error starting wifi scan: ${e.message}")
            }
        }

        if (MainActivity.ipConnect.isEmpty() || MainActivity.btContent.isEmpty()) {

            //判断SDK 是否有连接

            ipConnectList = PrinterHelper.getInstance().getPrinterInfoList(WifiKeyName.WIFI_ALL_CONNECT_IP)
            Log.d(
                TAG,
                "initData==ipConnectList: ${ipConnectList?.size}  "
            )
//            PrinterHelper.getInstance().getPrinterInfo(
//                WifiKeyName.WIFI_ALL_CONNECT_IP,
//                object : INeoPrinterCallback() {
//                    @Throws(RemoteException::class)
//                    override fun onRunResult(b: Boolean) {
//                    }
//
//                    @Throws(RemoteException::class)
//                    override fun onReturnString(s: String) {
//                        Log.d(
//                            TAG,
//                            "initData==CURRENT_CONNECT_WIFI_IP: $s  "
//                        )
//                        activity?.runOnUiThread {
//                            if (!Utils.isEmpty(s)) {
//                                MainActivity.ipConnect = s
//                            }
//                        }
//                    }
//
//                    @Throws(RemoteException::class)
//                    override fun onRaiseException(i: Int, s: String) {
//                    }
//
//                    @Throws(RemoteException::class)
//                    override fun onPrintResult(i: Int, s: String) {
//                    }
//                })

            PrinterHelper.getInstance().getPrinterInfo(
                WifiKeyName.BT_CURRENT_CONNECT_MAC,
                object : INeoPrinterCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(b: Boolean) {
                    }

                    @Throws(RemoteException::class)
                    override fun onReturnString(s: String) {
                        Log.d(
                            TAG,
                            "initData==BT_CURRENT_CONNECT_MAC: $s  "
                        )
                        activity?.runOnUiThread {
                            if (!Utils.isEmpty(s)) {
                                MainActivity.btContent = s
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

    }

    private suspend fun startDnsScan() {
//        MdnsDiscovery.startDiscovery("_ipp._tcp.")
//        MdnsDiscovery.discoveryEvents.collect { event ->
//            when (event) {
//                is MdnsDiscovery.DiscoveryEvent.ServiceFound -> {
//                    val device = event.device
//                    Log.d(TAG,
//                        "发现设备：名称：${device.serviceName}  " +
//                                "IP：${device.hostAddress} 端口：${device.port} " +
//                                "类型：${device.serviceType} " +
//                                "TXT：${device.txtRecords}")
//
//                }
//                // 其他事件处理...
//                else -> {
//
//                }
//            }
//        }
        // 监听 HTTP 和打印机服务
        serviceHelper.startDiscovery("_http._tcp.local.", "_printer._tcp.local.")
    }

    private fun discoverMdnsServices() {
        try {
            // 获取设备的所有网络接口
            val networkInterfaces = NetworkInterface.getNetworkInterfaces().asSequence().toList()

            for (networkInterface in networkInterfaces) {
                for (inetAddress in networkInterface.inetAddresses) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress) {
                        JmDNS.create(inetAddress).use { jmdns ->
                            // 添加服务监听器
                            jmdns.addServiceListener("_http._tcp.local.", object : ServiceListener {
                                override fun serviceAdded(event: ServiceEvent) {
                                    Log.d(TAG, "Service added: ${event.info}")
                                }

                                override fun serviceRemoved(event: ServiceEvent) {
                                    Log.d(TAG, "Service removed: ${event.info}")
                                }

                                override fun serviceResolved(event: ServiceEvent) {
                                    Log.d(TAG, "Service resolved: ${event.info}")
                                    val serviceInfo: ServiceInfo = event.info
                                    Log.d(TAG, "Service info: $serviceInfo")
                                }
                            })

                            // 保持监听一段时间，比如10秒钟
                            Thread.sleep(10000)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun startBleScan() {
        BluetoothScanner.startLeScanWithCoroutines(this, this)
    }

    private fun disConnectWifi() {
        PrinterHelper.getInstance()
            .setPrinterAction(
                WifiKeyName.WIFI_DISCONNECT,
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
                        Log.d(TAG, "DISCONNECT_WIFI==:  $s")
                        activity!!.runOnUiThread {
//                        binding.wifiStatusTv.text =
//                            String.format(
//                                getString(R.string.status_wifi),
//                                "WIFI",
//                                getString(R.string.un_connected)
//                            )
//                        binding.wifiIPTv.text =
//                            String.format(getString(R.string.status_ip), "-------")
                            MainActivity.ipConnect = ""
                        }
                    }
                })
    }

    private fun disConnectBt() {
        PrinterHelper.getInstance()
            .setPrinterAction(
                WifiKeyName.BT_DISCONNECT,
                MainActivity.btContent,
                object : INeoPrinterCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(b: Boolean) {
                    }

                    @Throws(RemoteException::class)
                    override fun onReturnString(s: String) {
                        Log.d(TAG, "bt disconnect==:  $s  ")
                    }

                    @Throws(RemoteException::class)
                    override fun onRaiseException(i: Int, s: String) {
                    }

                    @Throws(RemoteException::class)
                    override fun onPrintResult(i: Int, s: String) {
                        Log.d(TAG, "bt disconnect==:  $s   ,i=  $i")
                        activity!!.runOnUiThread {
                            // 查找第一个匹配设备地址的索引
                            val targetIndex = connectedBtList.indexOfFirst { it.address == MainActivity.btContent }  //:ml-citation{ref="7" data="citationList"}
                            if (targetIndex != -1) {
                                adapterBtConnect.removeAt(targetIndex)  // 精准移除特定设备条目:ml-citation{ref="7,8" data="citationList"}
                                connectedBtList.removeAt(targetIndex)
                            }
                            MainActivity.btContent = ""
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
            disConnectWifi()
        }
    }

    override fun onDestroyView() {
        wifiScanner.release()
        ExecutorServiceManager.shutdownExecutorService()
        BluetoothScanner.stopLeScanCompletely()
        MdnsDiscovery.destroy()
        serviceHelper.cleanup()
        super.onDestroyView()
    }


    override fun onDeviceFound(deviceAddress: String, deviceName: String) {

    }

    override fun onManufacturerDataParsed(deviceInfo: BluetoothDeviceInfo) {
        if (deviceInfo != null) {
            when (deviceInfo.wifiConnectStatus) {
                1 -> {//已经配网

                    if (unConnectList.isNotEmpty() && unConnectList.size > 0) {
                        // 查找匹配的设备索引
                        val index = unConnectList.indexOfFirst { it.address == deviceInfo.address }

                        if (index != -1) {
                            // 替换旧设备信息为新设备信息
                            unConnectList[index] = deviceInfo
                            adapterUnConnect.notifyItemChanged(index, deviceInfo)
                        } else {
                            println("No matching device found")
                            unConnectList.add(deviceInfo)
                            adapterUnConnect.addData(deviceInfo)
                        }
                    } else {

                        unConnectList.add(deviceInfo)
                        adapterUnConnect.addData(deviceInfo)
                    }
//                    adapterUnConnect.notifyDataSetChanged()

                    if (unWifiList.isNotEmpty() && unWifiList.size > 0) {
                        val index = unWifiList.indexOfFirst { it.address == deviceInfo.address }
                        if (index != -1) {
                            unWifiList.removeAt(index)
                            //adapterUnWifi.notifyDataSetChanged()
                            adapterUnWifi.remove(index)
                        }
                    }

                    ipConnectList = PrinterHelper.getInstance().getPrinterInfoList(WifiKeyName.WIFI_ALL_CONNECT_IP)

                    if (ipConnectList != null && ipConnectList!!.isNotEmpty()){
                        if (ipConnectList!!.contains(deviceInfo.ipAddress)){
                            val targetIndex = connectedWifiList.indexOfFirst { it.address == deviceInfo.address }
                            if (targetIndex != -1) {
                                // 替换现有设备信息
                                connectedWifiList[targetIndex] = deviceInfo
                                adapterWifiConnect.notifyItemChanged(targetIndex,deviceInfo)
                            } else {
                                // 新增设备（支持列表为空时的首次添加）
                                connectedWifiList.add(deviceInfo)
                                adapterWifiConnect.addData(deviceInfo)
                            }

                            //更新已配网的数据列表
                            if (unConnectList.isNotEmpty() && unConnectList.size > 0) {
                                val index = unConnectList.indexOfFirst { it.address == deviceInfo.address }
                                if (index != -1) {
                                    unConnectList.removeAt(index)
                                    adapterUnConnect.remove(index)
                                }
                            }


                        }else{
                            val targetIndex = connectedWifiList.indexOfFirst { it.address == deviceInfo.address }
                            if (targetIndex != -1) {
                                // 替换现有设备信息
                                connectedWifiList.removeAt(targetIndex)
                                adapterWifiConnect.remove(targetIndex)
                            }
                        }
                    }
                }

                0 -> {//没有配网
                    if (unWifiList.isNotEmpty() && unWifiList.size > 0) {
                        // 查找匹配的设备索引
                        val index = unWifiList.indexOfFirst { it.address == deviceInfo.address }

                        if (index != -1) {
                            // 替换旧设备信息为新设备信息
                            unWifiList[index] = deviceInfo
                            adapterUnWifi.notifyItemChanged(index, deviceInfo)
                        } else {
                            println("No matching device found")
                            unWifiList.add(deviceInfo)
                            adapterUnWifi.addData(deviceInfo)
                        }
                    } else {
                        unWifiList.add(deviceInfo)
                        adapterUnWifi.addData(deviceInfo)
                    }


                }
            }

            if (MainActivity.btContent == deviceInfo.address) {
                if (connectedBtList.isEmpty()) {
                    connectedBtList.add(deviceInfo)
                    adapterBtConnect.replaceData(connectedBtList)
                } else {
                    connectedBtList.fill(deviceInfo)
                    adapterBtConnect.notifyItemChanged(0, deviceInfo)
                }

            }


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