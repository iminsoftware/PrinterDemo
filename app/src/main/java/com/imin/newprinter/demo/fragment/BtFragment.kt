package com.imin.newprinter.demo.fragment

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.imin.newprinter.demo.MainActivity
import com.imin.newprinter.demo.R
import com.imin.newprinter.demo.bean.BluetoothBean
import com.imin.newprinter.demo.callback.SwitchFragmentListener
import com.imin.newprinter.demo.databinding.FragmentBtConnectBinding
import com.imin.newprinter.demo.utils.ExecutorServiceManager
import com.imin.newprinter.demo.utils.LoadingDialogUtil
import com.imin.newprinter.demo.utils.WifiKeyName
import com.imin.printer.INeoPrinterCallback
import com.imin.printer.PrinterHelper
import java.util.*

@SuppressLint("MissingPermission")
class BtFragment : BaseFragment() {
    private val TAG = "PrintDemo_BtFragment"
    private lateinit var binding: FragmentBtConnectBinding
    private val OPEN_BLUETOOTH_REQUEST_CODE = 0x00
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var manager: LocationManager? = null
    private val pairedDevices = ArrayList<BluetoothBean>()
    private val newDevices = ArrayList<BluetoothBean>()
    private var boundDevices = HashSet<BluetoothDevice>()
    private lateinit var adapter: BaseQuickAdapter<BluetoothBean, BaseViewHolder>
    private lateinit var adapter2: BaseQuickAdapter<BluetoothBean, BaseViewHolder>
    private var fragmentListener: SwitchFragmentListener? = null
    private var aBoolean = true

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val bean = msg.obj as? BluetoothBean ?: return
            when (msg.what) {
                100 -> addNewBlueTooth(bean)//未配对
                else -> updateBlueToothSignal(bean)//配对
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    binding.srlRefresh.finishRefresh()
                    adapter.notifyDataSetChanged()
                    adapter2.notifyDataSetChanged()
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, Int.MIN_VALUE)) {
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            binding.srlRefresh.finishRefresh()
                            (activity as? MainActivity)?.disConnectWirelessPrint()
                            disConnect()
                        }
                    }
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (it.bluetoothClass.majorDeviceClass != 1536) return  /*||
                            it.name?.contains("80mm Wireless") != true)*/

                        val bean = BluetoothBean().apply {
                            bluetoothName = it.name ?: "unKnow"
                            bluetoothMac = it.address
                            bluetoothStrength = "${intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0)}"
                        }

                        Log.d(
                            TAG, """
     onReceive: 
     BlueToothName:	${device.name}
     MacAddress:	${device.address}
     rssi:	${bean.bluetoothStrength}
     """.trimIndent()
                        )

                        mainHandler.sendMessage(Message.obtain().apply {
                            obj = bean
                            what = if (it.bondState != BluetoothDevice.BOND_BONDED) 100 else 101
                        })
                    }
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d(TAG,
            "setUserVisibleHint: $isVisibleToUser    $isResumed"
        )
        if (isVisibleToUser && isResumed) initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBtConnectBinding.inflate(inflater)
        initView()
        initData()
        return binding.root
    }

    private fun initView() {
        binding.lvBluetoothDevice.layoutManager = LinearLayoutManager(context)
        binding.unPairedDeviceLv.layoutManager = LinearLayoutManager(context)

        adapter = object : BaseQuickAdapter<BluetoothBean, BaseViewHolder>(R.layout.bluetooth_list_item) {
            override fun convert(holder: BaseViewHolder, item: BluetoothBean) {
                holder.getView<TextView>(R.id.b_name).text = item.bluetoothName
                holder.getView<TextView>(R.id.b_mac).text = item.bluetoothMac
                holder.getView<TextView>(R.id.b_info).text = item.bluetoothStrength
                holder.getView<RelativeLayout>(R.id.itemRl).background =
                    if (item.bluetoothMac == MainActivity.btContent)
                        resources.getDrawable(R.drawable.btn_green1_shap)
                    else null
            }
        }

        adapter2 = object : BaseQuickAdapter<BluetoothBean, BaseViewHolder>(R.layout.bluetooth_list_item) {
            override fun convert(holder: BaseViewHolder, item: BluetoothBean) {
                holder.getView<TextView>(R.id.b_name).text = item.bluetoothName
                holder.getView<TextView>(R.id.b_mac).text = item.bluetoothMac
                holder.getView<TextView>(R.id.b_info).text = item.bluetoothStrength
                holder.getView<RelativeLayout>(R.id.itemRl).background =
                    if (item.bluetoothMac == MainActivity.btContent)
                        resources.getDrawable(R.drawable.btn_green1_shap)
                    else null
            }
        }

        binding.lvBluetoothDevice.adapter = adapter
        binding.unPairedDeviceLv.adapter = adapter2

        adapter.setOnItemClickListener { _, _, position ->
            pairedDevices.getOrNull(position)?.let {
                mBluetoothAdapter?.cancelDiscovery()
                connectBt(it.bluetoothMac, it.bluetoothName)
            }
        }

        adapter2.setOnItemClickListener { _, _, position ->
            newDevices.getOrNull(position)?.let {
                mBluetoothAdapter?.cancelDiscovery()
                connectBt(it.bluetoothMac, it.bluetoothName)
            }
        }

        binding.srlRefresh.setOnRefreshListener {
            searchBtData()
            it.finishRefresh(5000)
        }

        binding.searchBt.setOnClickListener { searchBtData() }
        binding.viewTitle.setRightCallback { switchFragment(100) }
        binding.btDisconnect.setOnClickListener { disConnect() }

        registerBlueToothBroadcast()
    }

    private fun connectBt(mac: String, name: String) {
        LoadingDialogUtil.getInstance().show(requireContext(), "")
        MainActivity.connectAddress = mac
        MainActivity.btContent = mac

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
                }
            })
        PrinterHelper.getInstance().setPrinterAction(
            WifiKeyName.BT_CONNECT_MAC,
            MainActivity.btContent,
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
                    Log.d(TAG, "WIFI_CONNECT=>$s  i=$i")
                    activity!!.runOnUiThread {
                        if (i == 1) {
                            MainActivity.connectType = "BT"
                            MainActivity.connectContent = name + "\t" + mac
                            switchFragment(4)
                        } else {
                            Toast.makeText(
                                context,
                                getText(R.string.connect_fail), Toast.LENGTH_LONG
                            ).show()
                        }
                        LoadingDialogUtil.getInstance().hide()
                    }
                }
            })


    }

    private fun initData() {
        binding.btStatusTv.text = String.format(
            getString(R.string.status_wifi),
            "BT",
            if (MainActivity.btContent.isEmpty())
                getString(R.string.un_connected)
            else getString(R.string.connected)
        )

        if (checkBluetoothPermissions()) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            mBluetoothAdapter?.let {
                if (!it.isEnabled) {
                    startActivityForResult(
                        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                        OPEN_BLUETOOTH_REQUEST_CODE
                    )
                } else {
                    if (checkBluetoothPermissions()) searchBtData()
                }
            }
        }
    }

    private fun searchBtData() {
        mBluetoothAdapter?.let {
            if (!it.isEnabled) {
                startActivityForResult(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    OPEN_BLUETOOTH_REQUEST_CODE
                )
                return
            }
            searchBlueTooth()
        }
    }

    private fun registerBlueToothBroadcast() {
        try {
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)//搜索蓝牙设备
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)//蓝牙搜索完成
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//蓝牙状态改变
            })
        } catch (e: Exception) {
            Log.e(TAG, "register bluetooth error: ${e.message}")
        }
    }

    private fun getBoundDevices() {
        pairedDevices.clear()
        mBluetoothAdapter?.bondedDevices?.forEach { device ->
            if (device.name?.contains("80mm Wireless Printer") == true) {
                pairedDevices.add(BluetoothBean().apply {
                    bluetoothName = device.name
                    bluetoothMac = device.address
                })
            }
        }
        adapter.replaceData(pairedDevices)
    }

    private fun addNewBlueTooth(bean: BluetoothBean) {
        if (newDevices.none { it.bluetoothMac == bean.bluetoothMac }) {
            newDevices.add(bean)
            newDevices.sortWith(compareBy { it.bluetoothStrength })
            adapter2.replaceData(newDevices)
        }
    }

    private fun updateBlueToothSignal(bean: BluetoothBean) {
        Log.d(TAG, "updateBlueToothSignal=>")
        pairedDevices.firstOrNull { it.bluetoothMac == bean.bluetoothMac }?.let {
            it.bluetoothStrength = bean.bluetoothStrength
            adapter.notifyItemChanged(pairedDevices.indexOf(it))
        } ?: run {
            pairedDevices.add(bean)
            adapter.replaceData(pairedDevices)
        }
        Log.d(TAG, "updateBlueToothSignal=11>")

    }

    private fun disConnect() {
        PrinterHelper.getInstance()
            .setPrinterAction(WifiKeyName.BT_DISCONNECT, "", object : INeoPrinterCallback() {
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
                    Log.d(TAG, "bt disconnect==:  $s   ,i=  $i")
                    activity!!.runOnUiThread {
                        binding.btStatusTv.text =
                            String.format(
                                getString(R.string.status_wifi),
                                "BT",
                                getString(R.string.un_connected)
                            )
                        MainActivity.btContent = ""
                    }
                }
            })
    }

    private fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    private fun searchBlueTooth() {
        mBluetoothAdapter?.takeIf { !it.isDiscovering }?.startDiscovery()

//        if (aBoolean) {
//            aBoolean = false
//            ExecutorServiceManager.getExecutorService().submit {
//                mBluetoothAdapter?.startLeScan { device, _, scanRecord ->
//                    device?.let {
//                        Log.d(TAG, "onLeScan: ${it.address} - ${it.name}")
//                        parseScanRecord(scanRecord)
//                    }
//                }
//            }
//        }
    }

    private fun parseScanRecord(scanRecord: ByteArray?) {
        scanRecord?.let {
            var index = 0
            while (index < it.size) {
                val length = it[index++].toInt() and 0xFF
                if (length == 0) break

                val type = it[index++].toInt() and 0xFF
                val data = it.copyOfRange(index, index + length - 1)
                index += length - 1

                when (type) {
                    0x09 -> Log.d(TAG, "Device Name: ${String(data)}")
                    0x03 -> {} // 处理UUID
                    0xFF -> {} // 处理厂商数据
                }
            }
        }
    }

    fun setCallback(listener: SwitchFragmentListener) {
        fragmentListener = listener
    }
    public fun switchFragment(num: Int) {
        fragmentListener?.switchFragment(num)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mBluetoothAdapter?.cancelDiscovery()
            requireActivity().unregisterReceiver(broadcastReceiver)
            pairedDevices.clear()
            newDevices.clear()
            boundDevices.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class Signal : Comparator<BluetoothBean> {
        override fun compare(p1: BluetoothBean, p2: BluetoothBean): Int {
            return p1.bluetoothStrength.compareTo(p2.bluetoothStrength)
        }
    }

    @Synchronized
    fun cancelSearchBlueTooth() {
        ExecutorServiceManager.getExecutorService().submit {
            try {
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter!!.isDiscovering) {
                        mBluetoothAdapter!!.cancelDiscovery() //取消搜索
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Error playing audio: " + e.message)
            }
        }
    }
}