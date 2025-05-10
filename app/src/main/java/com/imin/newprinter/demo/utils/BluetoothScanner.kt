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
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.imin.newprinter.demo.IminApplication
import com.imin.newprinter.demo.bean.BluetoothDeviceInfo
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("MissingPermission")


object BluetoothScanner {
    public const val TAG = "PrintDemo_BluetoothScanner"
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
    private var scanWait: Long = 5000 // 默认低功耗模式
    private lateinit var currentScanMode: ScanMode

    private var scanJob: Job? = null
//    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    // 修改后的协程作用域（限制并发线程数）
    private val coroutineScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(1) + // 限制最大并发协程数
                SupervisorJob() +
                CoroutineExceptionHandler { _, e ->
                    Log.e(TAG, "Coroutine error", e)
                }
    )

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
                scanPeriod = 10000//30000
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

    private fun startBleScan() {
        Log.d(TAG, "Attempting to start BLE scan, isScanning=$isScanning")
        // 取消之前的扫描任务
//        scanPeriod = 5000
        scanJob?.cancel()
        scanJob = null
        scanJob = coroutineScope.launch {
            val settings = ScanSettings.Builder()
                .setScanMode(getScanMode())
                .setMatchMode(ScanSettings.SCAN_MODE_LOW_LATENCY)// MATCH_MODE_AGGRESSIVE积极匹配模式
                .setReportDelay(0)                               // 实时返回结果
                .build()

            val filters = listOf(
                ScanFilter.Builder()
                    .setManufacturerData(0x2D0A, null) // 明确设置厂商ID过滤 // 空数组匹配任意数据
                    .build()
            )

            try {
                Log.e(TAG, "Starting actual BLE scan...")
                bluetoothLeScanner?.startScan(filters, settings, scanCallback)
                isScanning = true
                // 优化扫描周期处理逻辑
                when {
                    scanPeriod > 0 -> {
                        delay(scanPeriod)
                        stopBleScan()
                        Log.d(TAG, "Completed scan period, scanInterval=$scanInterval")

                        if (scanInterval > 0) {
                            delay(scanWait)
                            Log.d(TAG, "Restarting scan after interval")
                            startBleScan()
                        }
                    }
                    scanInterval == 1L -> {
                        // 持续扫描模式：使用无限延迟保持协程活动
                        while (isActive) {
                            delay(Long.MAX_VALUE) // 保持协程不退出即可
                        }
                    }
                }

//                // 处理扫描周期逻辑
//                if (scanPeriod > 0) {
//                    delay(scanPeriod)
//                    stopBleScan()
//                    Log.e(TAG, "Completed scan period, scanInterval=$scanInterval")
//
//                    if (scanInterval > 0) {
//                        delay(scanWait)
//                        Log.e(TAG, "Restarting scan after interval")
//                        //startBleScan() // 递归调用重启扫描
//                        restartBleScanAfterDelay()
//                    }
//                } else if (scanInterval == 1L) {
//                    // 持续扫描模式
//                    while (isActive) {
//                        delay(Long.MAX_VALUE)
//                        Log.e(TAG, "Continuous scanning...")
//                    }
//                }
            } catch (e: Exception) {
                Log.e(TAG, "BLE scan failed: ${e.message}")
                // 异常后尝试重启扫描
                handleScanException(e)
            }
        }
    }


    // 优化后的扫描控制方法
    private fun restartScanWithDelay(delay: Long) {
        coroutineScope.launch {
            delay(delay)
            if (isScanning) {
                Log.d(TAG, "Restarting scan...")
                stopBleScan()
            }
            startBleScan()
        }
    }

    // 优化后的异常处理
    private fun handleScanException(e: Exception) {
        Log.e(TAG, "Scan error: ${e.message}")
        restartScanWithDelay(2000)
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
        bluetoothLeScanner?.stopScan(scanCallback)
        coroutineScope.coroutineContext.cancelChildren()
        discoveredDevices.clear()
        callback = null
      //  scanPeriod = 0
        Log.d(TAG, "Completely stopped and resources cleaned")
    }


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
                CoroutineScope(Dispatchers.Default).launch {
//                    Log.d(TAG, "onLeScan: ${device.address} - ${device.name}")
//                    Log.d(TAG, "onLeScan: address=> ${device.address}, name=> ${device.name}, BondState=> ${device.bondState}, hex=> ${BytesUtils.bytesToHex(result.scanRecord?.bytes ?: byteArrayOf())}, byte=> ${Arrays.toString(result.scanRecord?.bytes)}")

                    result.scanRecord?.bytes?.let { scanRecord ->
                        try {
                            parseScanRecord(scanRecord, device, result.rssi)
                        } catch (e: Exception) {
                            Log.w(TAG, "Parse error: ${e.message}")
                        }
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
            restartScanWithDelay(1000)
        }
    }

    // 优化后的数据解析方法
    // 修复5：优化数据解析边界检查
    // 保持原始数据解析逻辑（关键功能）
    private suspend fun parseScanRecord(
        scanRecord: ByteArray,
        device: BluetoothDevice,
        rssiBle: Int
    ) {
        var index = 0
        try {
            while (index < scanRecord.size) {
                val length = scanRecord[index++].toInt() and 0xFF
                if (length == 0) break

                val type = scanRecord[index++].toInt() and 0xFF
                if (index + length - 1 > scanRecord.size) break

                if (type == 0xFF) { // Manufacturer data
                    val manufacturerId = ((scanRecord[index].toInt() and 0xFF) shl 8) or
                            (scanRecord[index + 1].toInt() and 0xFF)
                    if (manufacturerId == 0x2D0A) {
                        // 保持原始解析逻辑
                        val data = scanRecord.copyOfRange(index, index + length - 1)
                        processManufacturerData(data, device, rssiBle)
                    }
                }
//                else if (type == 0x03) {
//                    val uuidList = mutableListOf<UUID>()
//                    for (i in scanRecord.indices step 2) {
//                        val uuid = ((scanRecord[i + 1].toInt() and 0xFF) shl 8) or (scanRecord[i].toInt() and 0xFF)
//                        uuidList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid)))
//                    }
//                    Log.d(TAG, "onLeScan :16-bit UUIDs=  $uuidList  ")
//                }
                index += length - 1
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.w(TAG, "Invalid scan record format")
        }
    }

    // 原始数据处理方法
    private fun processManufacturerData(data: ByteArray, device: BluetoothDevice, rssiBle: Int) {
        if (data.size < 14) return

        try {
            Log.d(TAG, "onLeScan :data.size=  ${data.size}, devicesName=${device.name}, deviceAddress=${device.address},\n" +
                        " hex=> ${BytesUtils.bytesToHex(data)}")
            val wifiStatus = data[2].toInt() and 0xFF
            val printerStatus = data[3].toInt() and 0xFF
            val ioBits = (data[4].toInt() and 0x07).toString(2).padStart(3, '0')
            val ipAddress = "${data[5].toInt() and 0xFF}.${data[6].toInt() and 0xFF}." +
                    "${data[7].toInt() and 0xFF}.${data[8].toInt() and 0xFF}"
            val rssiWifi = data[9].toInt()
            val wifiName = String(data, 10, 4)

            Log.d(TAG, "onLeScan :WiFi Status= $wifiStatus, " +
                                        "Printer Status= $printerStatus, IO Status= $ioBits, IP Address= $ipAddress, rssiWifi= $rssiWifi, WiFi Name= $wifiName" +
                                        ", rssiBle= $rssiBle , device.name=${device.name} ,device.address= ${device.address} ")
            val deviceInfo = BluetoothDeviceInfo(
                name = device.name?.replace(" LE", "")?: "Unknown",
                address = "DC" + device.address.substring(2) ,
                rssiBle = rssiBle,
                rssiWifi = rssiWifi,
                wifiConnectStatus = wifiStatus,
                printerWorkingStatus = printerStatus,
                ioStatus = ioBits,
                ipAddress = ipAddress,
                wifiName = wifiName
            )

            CoroutineScope(Dispatchers.Main).launch {
                callback?.onManufacturerDataParsed(deviceInfo)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Data parsing failed: ${e.message}")
        }
    }

//    private suspend fun parseScanRecord(
//        scanRecord: ByteArray,
//        device: BluetoothDevice,
//        rssiBle: Int
//    ) {
//        var index = 0
//        while (index < scanRecord.size) {
//            val length = scanRecord[index++].toInt() and 0xFF
//            if (length == 0) break
//            val type = scanRecord[index++].toInt() and 0xFF
//            val data = Arrays.copyOfRange(scanRecord, index, index + length - 1)
//            index += length - 1
//
//            when (type) {
//                0xFF -> {
//                    if (data.size < 14) return
//                    val deviceName = device.name ?: ""
//                    if (deviceName.isEmpty())return
////                    Log.d(
////                        TAG,
////                        "onLeScan :data.size=  ${data.size}, devicesName=$deviceName, deviceAddress=${device.address}"
////                    )
//                    if (data.size >= 14) {
//                        val manufacturerId =
//                            ((data[0].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
////                        Log.d(
////                            TAG,
////                            "onLeScan Manufacturer ID: ${String.format("%04X", manufacturerId)} "
////                        )
//
//                        if (manufacturerId == 0x2D0A) { // 替换为你需要的厂商ID
//                            val deviceName = device.name ?: ""
//                            Log.d(
//                                TAG,
//                                "onLeScan :data.size=  ${data.size}, devicesName=$deviceName, deviceAddress=${device.address},\n" +
//                                        " hex=> ${BytesUtils.bytesToHex(scanRecord)}"
//                            )
//                            Log.d(
//                                TAG,
//                                "onLeScan :data[0] WiFi Status=  ${data[2]}, printerWorkingStatus=${data[3]}, ioStatus=${data[4]}"
//                            )
//                            val wifiConnectStatus = data[2].toInt() and 0xFF
//                            val printerWorkingStatus = data[3].toInt() and 0xFF
//
//                            val targetByte = data[4]
//                            val ioBits = targetByte.toInt() and 0x07
//                            val binaryString = Integer.toBinaryString(ioBits)
//                            val ioStatus = binaryString.padStart(3, '0') // 直接补零到3位
//                            // 输入0x03 → 输出011
//                            // 输入0x01 → 输出001
//                            Log.d(TAG, "Parsed IO Status: $ioStatus")
//                            // IP地址解析（4字节 @5-8）
//                            val ipAddress = buildString {
//                                append(data[5].toInt() and 0xFF)
//                                append('.')
//                                append(data[6].toInt() and 0xFF)
//                                append('.')
//                                append(data[7].toInt() and 0xFF)
//                                append('.')
//                                append(data[8].toInt() and 0xFF)
//                            }
//
//                            val rssiWifi = data[9].toInt()
//                            Log.d(TAG, ",device.address= ${device.address} data[9] : ${data[9]} ")
//                            val wifiNameBytes = data.copyOfRange(10, 14)
//                            val wifiName = String(wifiNameBytes)
//                            Log.d(
//                                TAG, "onLeScan :WiFi Status= $wifiConnectStatus, " +
//                                        "Printer Status= $printerWorkingStatus, IO Status= $ioStatus, IP Address= $ipAddress, rssiWifi= $rssiWifi, WiFi Name= $wifiName" +
//                                        ", rssiBle= $rssiBle , device.name=${device.name} ,device.address= ${device.address} "
//                            )
//
//                            // 封装成 BluetoothDeviceInfo 对象并通过回调返回
//                            val deviceInfo = BluetoothDeviceInfo(
//                                name = device.name ?: "Unknown",
//                                address = device.address,
//                                rssiBle = rssiBle,
//                                rssiWifi = rssiWifi,
//                                wifiConnectStatus = wifiConnectStatus,
//                                printerWorkingStatus = printerWorkingStatus,
//                                ioStatus = ioStatus,
//                                ipAddress = ipAddress,
//                                wifiName = wifiName
//                            )
//
//                            withContext(Dispatchers.Main) {
//                                callback?.onManufacturerDataParsed(deviceInfo)
//                            }
//
//
//                        } else {
////                            Log.d(TAG, "onLeScan :Invalid Manufacturer Data")
//                            return
//                        }
//
//                    } else {
//                        Log.d(TAG, "onLeScan :Invalid Manufacturer Data")
//                    }
//
//                }
//
//                else -> {
////                    Log.d(TAG, "Unknown AD Structure: type=$type, data=${data.joinToString(separator = " ") { String.format("%02X", it) }}")
//                }
//            }
//        }
//    }

    interface BluetoothScanCallback {

        fun onDeviceFound(deviceAddress: String, deviceName: String)
        fun onManufacturerDataParsed(deviceInfo: BluetoothDeviceInfo)
    }


}
