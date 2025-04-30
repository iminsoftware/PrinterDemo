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
    private var scanWait: Long = 2000 // 默认低功耗模式
    private lateinit var currentScanMode: ScanMode

    private val mBluetoothAdapter: BluetoothAdapter? by lazy {
        (IminApplication.mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val bluetoothLeScanner by lazy {
        mBluetoothAdapter?.bluetoothLeScanner
    }

    fun startLeScanWithCoroutines(fragment: Fragment, scanCallback: BluetoothScanCallback, mode: ScanMode = ScanMode.HIGH_FREQUENCY) {
        this.callback = scanCallback
        this.currentScanMode = mode
        when (mode) {
            ScanMode.LOW_POWER -> {
                scanPeriod = 10000
                scanInterval = 10000
            }
            ScanMode.BALANCED -> {
                scanPeriod = 20000
                scanInterval = 5000
            }
            ScanMode.HIGH_FREQUENCY -> {
                scanPeriod = 30000
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
            ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
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
        val manufacturerId = 123
        CoroutineScope(Dispatchers.Default).launch {
            val settings = ScanSettings.Builder()
                .setScanMode(getScanMode())
                .build()

//            val filters = listOf(
//                ScanFilter.Builder()
//                    .setManufacturerData(manufacturerId, null)
//                    .build()
//            )
            val filters = listOf(
                ScanFilter.Builder()
                    .build()
            )

            bluetoothLeScanner?.startScan(filters, settings, scanCallback)

            if (scanPeriod > 0) {
                Handler(Looper.getMainLooper()).postDelayed({
                    stopBleScan()
                    if (scanInterval > 0) {
                        // 等待一段时间后重新开始扫描
                        Handler(Looper.getMainLooper()).postDelayed({
                            startBleScan()
                        }, scanWait)
                    }
                }, scanPeriod)
            }
        }
    }

    private fun stopBleScan() {
        if (isScanning) {
            CoroutineScope(Dispatchers.Default).launch {
                bluetoothLeScanner?.stopScan(scanCallback)
                isScanning = false
                Log.d(TAG, "Stopped BLE scan")
            }
        }
    }

    fun stopLeScanCompletely() {
        if (isScanning) {
            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
            Log.d(TAG, "Stopped BLE scan completely")
        }
        discoveredDevices.clear()
        callback = null
    }

    private fun getScanMode(): Int {
        return when (currentScanMode) {
            ScanMode.LOW_POWER -> ScanSettings.SCAN_MODE_LOW_POWER
            ScanMode.BALANCED -> ScanSettings.SCAN_MODE_BALANCED
            ScanMode.HIGH_FREQUENCY -> ScanSettings.SCAN_MODE_LOW_LATENCY
        }
    }
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->

                if (discoveredDevices.contains(device.address)) {
                    return
                }

                discoveredDevices.add(device.address)


                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "onLeScan: ${device.address} - ${device.name}")
                    Log.d(TAG, "onLeScan: address=> ${device.address}, name=> ${device.name}, BondState=> ${device.bondState}, hex=> ${BytesUtils.bytesToHex(result.scanRecord?.bytes ?: byteArrayOf())}, byte=> ${Arrays.toString(result.scanRecord?.bytes)}")

                    result.scanRecord?.bytes?.let { scanRecord ->
                        parseScanRecord(scanRecord, device,result.rssi)
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
        }
    }

    private suspend fun parseScanRecord(scanRecord: ByteArray, device: BluetoothDevice,rssiBle: Int) {
        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt() and 0xFF
            if (length == 0) break
            val type = scanRecord[index++].toInt() and 0xFF
            val data = Arrays.copyOfRange(scanRecord, index, index + length - 1)
            index += length - 1

            when (type) {
                0x09 -> {
                    val deviceName = String(data)
                    Log.d(TAG, "onLeScan :deviceName=  $deviceName")
                }
                0x03 -> {
                    val uuidList = mutableListOf<UUID>()
                    for (i in data.indices step 2) {
                        val uuid = ((data[i + 1].toInt() and 0xFF) shl 8) or (data[i].toInt() and 0xFF)
                        uuidList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid)))
                    }
                    Log.d(TAG, "onLeScan :16-bit UUIDs=  $uuidList")
                }
                0xFF -> {
                    val deviceName = device.name ?: ""
                    Log.d(TAG, "onLeScan :WiFi Status=  ${data.size}, devicesName=$deviceName")
                    if (deviceName.contains("80mm")) {
                        if (data.size >= 12) {
                            val wifiConnectStatus = data[0].toInt() and 0xFF
                            val printerWorkingStatus = data[1].toInt() and 0xFF
                            val ioStatus = String.format("%03d", data[2].toInt() and 0xFF)

                            val ipAddressBytes = data.copyOfRange(3, 7)
                            val ipAddress = ipAddressBytes.joinToString(".") { (it.toInt() and 0xFF).toString() }

                            val rssiWifi = data[7].toInt() and 0xFF

                            val wifiNameBytes = data.copyOfRange(8, 12)
                            val wifiName = String(wifiNameBytes)

                            Log.d(TAG, "onLeScan :WiFi Status= $wifiConnectStatus, " +
                                    "Printer Status= $printerWorkingStatus, IO Status= $ioStatus, IP Address= $ipAddress, rssiWifi= $rssiWifi, WiFi Name= $wifiName" +
                                    ", rssiBle= $rssiBle , device.name=${device.name} ,device.address= ${device.address} ")

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
                }
                else -> {
                    Log.d(TAG, "Unknown AD Structure: type=$type, data=${data.joinToString(separator = " ") { String.format("%02X", it) }}")
                }
            }
        }
    }

    interface BluetoothScanCallback {

        fun onDeviceFound(deviceAddress: String, deviceName: String)
        fun onManufacturerDataParsed(deviceInfo: BluetoothDeviceInfo)
    }


}
