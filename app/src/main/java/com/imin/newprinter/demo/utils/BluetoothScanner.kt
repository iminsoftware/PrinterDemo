package com.imin.newprinter.demo.utils

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.imin.newprinter.demo.IminApplication
import com.imin.newprinter.demo.bean.BluetoothDeviceInfo
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("MissingPermission")


object BluetoothScanner {
    private const val TAG = "PrintDemo_BluetoothScanner"
    const val REQUEST_ENABLE_BT = 1
    const val REQUEST_CODE_PERMISSIONS = 2

    enum class ScanMode {
        LOW_POWER, BALANCED, HIGH_FREQUENCY
    }

    private val discoveredDevices = HashSet<String>()
    private var isScanning = false
    private var callback: BluetoothScanCallback? = null

    private var scanPeriod: Long = 10000 // 默认低功耗模式
    private var scanInterval: Long = 10000 // 默认低功耗模式
    private var scanWait: Long = 100 // 默认低功耗模式
    private lateinit var currentScanMode: ScanMode

    private var scanJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val mBluetoothAdapter: BluetoothAdapter? by lazy {
        (IminApplication.mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val bluetoothLeScanner by lazy {
        mBluetoothAdapter?.bluetoothLeScanner
    }

    fun startLeScanWithCoroutines(
        fragment: Fragment,
        scanCallback: BluetoothScanCallback,
        mode: ScanMode = ScanMode.HIGH_FREQUENCY
    ) {
        this.callback = scanCallback
        this.currentScanMode = mode
        when (mode) {
            ScanMode.LOW_POWER -> {
                scanPeriod = 5000
                scanInterval = 5000
            }

            ScanMode.BALANCED -> {
                scanPeriod = 10000
                scanInterval = 3000
            }

            ScanMode.HIGH_FREQUENCY -> {
                scanPeriod = 5000//30000
                scanInterval = 1 // 连续扫描
            }
        }

        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth")
            return
        }

        if (!mBluetoothAdapter?.isEnabled!!) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            fragment.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        if (!hasPermissions(fragment)) {
            requestPermissions(fragment)
            return
        }

        if (!isScanning) {
            Log.d(TAG, "Starting BLE scan")
            isScanning = true
            startBleScan()
        }
    }

    private fun hasPermissions(fragment: Fragment): Boolean {
        val requiredPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions(fragment: Fragment) {
        val requiredPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        fragment.requestPermissions(
            requiredPermissions.toTypedArray(),
            REQUEST_CODE_PERMISSIONS
        )
    }

//    private fun startBleScan() {
//        val manufacturerId = 0x2D0A
//        CoroutineScope(Dispatchers.Default).launch {
//            val settings = ScanSettings.Builder()
//                .setScanMode(getScanMode())
//                .build()
//
////            val filters = listOf(
////                ScanFilter.Builder()
////                    .setManufacturerData(manufacturerId, null)
////                    .build()
////            )
//            val filters = listOf(
//                ScanFilter.Builder()
////                    .setManufacturerData(manufacturerId, byteArrayOf())  // 掩码（仅首字节生效）)
//                    .build()
//            )
//
//            bluetoothLeScanner?.startScan(filters, settings, scanCallback)
//
//            if (scanPeriod > 0) {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    stopBleScan()
//                    if (scanInterval > 0) {
//                        // 等待一段时间后重新开始扫描
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            Log.e(TAG, "重新扫描")
//                            startBleScan()
//                        }, scanWait)
//                    }
//                }, scanPeriod)
//            }else {
//                //继续扫描
//                if(scanInterval == 1L){
//                    startBleScan()
//                }
//            }
//        }
//    }

    private fun startBleScan() {
        Log.d(TAG, "Attempting to start BLE scan, isScanning=$isScanning")

        // 取消之前的扫描任务
        scanJob?.cancel()
        scanJob = coroutineScope.launch {
            val settings = ScanSettings.Builder()
                .setScanMode(getScanMode())
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)// 积极匹配模式
                .setReportDelay(0)                               // 实时返回结果
                .build()

            val filters = listOf(
                ScanFilter.Builder()
                    .setManufacturerData(0x2D0A, null) // 明确设置厂商ID过滤
                    .build()
            )

            try {
                Log.d(TAG, "Starting actual BLE scan...")
                bluetoothLeScanner?.startScan(filters, settings, scanCallback)
                isScanning = true

                // 处理扫描周期逻辑
                if (scanPeriod > 0) {
                    delay(scanPeriod)
                    stopBleScan()
                    Log.d(TAG, "Completed scan period, scanInterval=$scanInterval")

                    if (scanInterval > 0) {
                        delay(scanWait)
                        Log.d(TAG, "Restarting scan after interval")
                        startBleScan() // 递归调用重启扫描
                    }
                } else if (scanInterval == 1L) {
                    // 持续扫描模式
                    while (isActive) {
                        delay(1000)
                        Log.d(TAG, "Continuous scanning...")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "BLE scan failed: ${e.message}")
                // 异常后尝试重启扫描
                withContext(Dispatchers.Main) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.d(TAG, "Retrying scan after exception")
                        startBleScan()
                    }, 500)
                }
            }
        }
    }

    private fun stopBleScan() {
        if (isScanning) {
            Log.d(TAG, "Stopping BLE scan...")
            try {
                bluetoothLeScanner?.stopScan(scanCallback)
                isScanning = false
                discoveredDevices.clear()
                Log.d(TAG, "BLE scan stopped successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping BLE scan: ${e.message}")
            }
        }
    }

    fun stopLeScanCompletely() {
        Log.d(TAG, "Stopping completely...")
        scanJob?.cancel()
        stopBleScan()
        coroutineScope.coroutineContext.cancelChildren()
        discoveredDevices.clear()
        callback = null
        Log.d(TAG, "Completely stopped and resources cleaned")
    }

//    private fun stopBleScan() {
//        if (isScanning) {
//            CoroutineScope(Dispatchers.Default).launch {
//                bluetoothLeScanner?.stopScan(scanCallback)
//                isScanning = false
//                Log.e(TAG, "Stopped BLE scan")
//            }
//        }
//    }

//    fun stopLeScanCompletely() {
//        if (isScanning) {
//            bluetoothLeScanner?.stopScan(scanCallback)
//            isScanning = false
//            Log.e(TAG, "Stopped BLE scan completely")
//        }
//        discoveredDevices.clear()
//        callback = null
//    }

    private fun getScanMode(): Int {
        return when (currentScanMode) {
            ScanMode.LOW_POWER -> ScanSettings.SCAN_MODE_LOW_POWER
            ScanMode.BALANCED -> ScanSettings.SCAN_MODE_BALANCED
            ScanMode.HIGH_FREQUENCY -> ScanSettings.SCAN_MODE_LOW_LATENCY // 最高扫描频率
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->

                if (discoveredDevices.contains(device.address)) {
                }else{
                    discoveredDevices.add(device.address)
                }
                CoroutineScope(Dispatchers.IO).launch {
//                    Log.d(TAG, "onLeScan: ${device.address} - ${device.name}")
//                    Log.d(TAG, "onLeScan: address=> ${device.address}, name=> ${device.name}, BondState=> ${device.bondState}, hex=> ${BytesUtils.bytesToHex(result.scanRecord?.bytes ?: byteArrayOf())}, byte=> ${Arrays.toString(result.scanRecord?.bytes)}")

                    result.scanRecord?.bytes?.let { scanRecord ->
                        parseScanRecord(scanRecord, device, result.rssi)
                    }

                    withContext(Dispatchers.Main) {
                        callback?.onDeviceFound(device.address, device.name ?: "Unknown")
                    }
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.forEach { result ->
                onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan failed with error: $errorCode")
            // 扫描失败后自动重启
            Handler(Looper.getMainLooper()).post {
                if (!isScanning) {
                    Log.d(TAG, "Auto-restarting scan after failure")
                    startBleScan()
                }
            }
        }
    }

    private suspend fun parseScanRecord(
        scanRecord: ByteArray,
        device: BluetoothDevice,
        rssiBle: Int
    ) {
        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt() and 0xFF
            if (length == 0) break
            val type = scanRecord[index++].toInt() and 0xFF
            val data = Arrays.copyOfRange(scanRecord, index, index + length - 1)
            index += length - 1

            when (type) {
//                0x09 -> {
//                    val deviceName = String(data)
//                    Log.d(TAG, "onLeScan :deviceName=  $deviceName")
//                }
//
//                0x03 -> {
//                    val uuidList = mutableListOf<UUID>()
//                    for (i in data.indices step 2) {
//                        val uuid =
//                            ((data[i + 1].toInt() and 0xFF) shl 8) or (data[i].toInt() and 0xFF)
//                        uuidList.add(
//                            UUID.fromString(
//                                String.format(
//                                    "%08x-0000-1000-8000-00805f9b34fb",
//                                    uuid
//                                )
//                            )
//                        )
//                    }
//                    Log.d(TAG, "onLeScan :16-bit UUIDs=  $uuidList")
//                }

                0xFF -> {
                    val deviceName = device.name ?: ""
                    Log.d(
                        TAG,
                        "onLeScan :data.size=  ${data.size}, devicesName=$deviceName, deviceAddress=${device.address}"
                    )
                    if (data.size >= 12) {
                        val manufacturerId =
                            ((data[0].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
                        Log.d(TAG, "onLeScan Manufacturer ID: ${String.format("%04X", manufacturerId)} ")

                        if (manufacturerId == 0x2D0A) { // 替换为你需要的厂商ID
                            val deviceName = device.name ?: ""
                            Log.d(
                                TAG,
                                "onLeScan :data.size=  ${data.size}, devicesName=$deviceName, deviceAddress=${device.address},\n" +
                                        " hex=> ${BytesUtils.bytesToHex(scanRecord)}"
                            )
                            if (data.size >= 14) {
                                Log.d(
                                    TAG,
                                    "onLeScan :data[0] WiFi Status=  ${data[2]}, printerWorkingStatus=${data[3]}, ioStatus=${data[4]}"
                                )
                                val wifiConnectStatus = data[2].toInt() and 0xFF
                                val printerWorkingStatus = data[3].toInt() and 0xFF
                                // 修改后的解析逻辑
//                                        val targetByte = data[4]
//                                        val ioBits = targetByte.toInt() and 0x07 // 提取低3位
//                                        Log.d(TAG, "onLeScan :ioBits=  $ioBits ,devicesName=$deviceName,")
//                                        val ioStatus =  String.format("%03d", ioBits) // 例如 0x03 → 011

                                val targetByte = data[4]
                                val ioBits = targetByte.toInt() and 0x07
                                val binaryString = Integer.toBinaryString(ioBits)
                                val ioStatus = binaryString.padStart(3, '0') // 直接补零到3位
                                // 输入0x03 → 输出011
                                // 输入0x01 → 输出001
                                Log.d(TAG, "Parsed IO Status: $ioStatus")
                                // IP地址解析（4字节 @5-8）
                                val ipAddress = buildString {
                                    append(data[5].toInt() and 0xFF)
                                    append('.')
                                    append(data[6].toInt() and 0xFF)
                                    append('.')
                                    append(data[7].toInt() and 0xFF)
                                    append('.')
                                    append(data[8].toInt() and 0xFF)
                                }

                                val rssiWifi = data[9].toInt()
                                Log.d(TAG, ",device.address= ${device.address} data[9] : ${data[9]} ")
                                val wifiNameBytes = data.copyOfRange(10, 14)
                                val wifiName = String(wifiNameBytes)
                                // FF 2D0A 01 00  00 0A00173BCD 69 4D696E00  00000000000000000000000000B7000000
                                //FF 2D0A 01 00 00   0A00173B CE 69 4D696E0000000000000000000000000000B8000000
                                Log.d(
                                    TAG, "onLeScan :WiFi Status= $wifiConnectStatus, " +
                                            "Printer Status= $printerWorkingStatus, IO Status= $ioStatus, IP Address= $ipAddress, rssiWifi= $rssiWifi, WiFi Name= $wifiName" +
                                            ", rssiBle= $rssiBle , device.name=${device.name} ,device.address= ${device.address} "
                                )

                                // 封装成 BluetoothDeviceInfo 对象并通过回调返回
                                val deviceInfo = BluetoothDeviceInfo(
                                    name = device.name ?: "Unknown",
                                    address = device.address,
                                    rssiBle = rssiBle,
                                    rssiWifi = rssiWifi,
                                    wifiConnectStatus = wifiConnectStatus,
                                    printerWorkingStatus = printerWorkingStatus,
                                    ioStatus = ioStatus,
                                    ipAddress = ipAddress,
                                    wifiName = wifiName
                                )

                                withContext(Dispatchers.Main) {
                                    callback?.onManufacturerDataParsed(deviceInfo)
                                }
                            } else {
                                Log.d(TAG, "onLeScan :Invalid Manufacturer Data")
                            }

                        }

                    } else {
                        Log.d(TAG, "onLeScan :Invalid Manufacturer Data")
                    }

                }

                else -> {
//                    Log.d(TAG, "Unknown AD Structure: type=$type, data=${data.joinToString(separator = " ") { String.format("%02X", it) }}")
                }
            }
        }
    }

    interface BluetoothScanCallback {

        fun onDeviceFound(deviceAddress: String, deviceName: String)
        fun onManufacturerDataParsed(deviceInfo: BluetoothDeviceInfo)
    }


}
