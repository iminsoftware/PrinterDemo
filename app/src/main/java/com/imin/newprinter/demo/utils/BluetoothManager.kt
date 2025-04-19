package com.imin.newprinter.demo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executors

/**
 * @Author: hy
 * @date: 2025/4/2
 * @description:
 */
@SuppressLint("MissingPermission", "ServiceCast")
class BluetoothManager private constructor(private val context: Context) {
    // region 单例初始化
    companion object {
        @Volatile
        private var instance: BluetoothManager? = null

        fun getInstance(context: Context): BluetoothManager {
            return instance ?: synchronized(this) {
                instance ?: BluetoothManager(context.applicationContext).also { instance = it }
            }
        }
    }
    // endregion

    // region 成员变量
    // region 成员变量

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
// endregion

    // BLE 相关
    private var bleScanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    // SPP 相关
    private var sppSocket: BluetoothSocket? = null
    private var sppInputStream: InputStream? = null
    private var sppOutputStream: OutputStream? = null

    // 线程池
    private val executor = Executors.newCachedThreadPool()
    private val mainHandler = android.os.Handler(context.mainLooper)

    // 设备列表
    private val discoveredDevices = mutableMapOf<String, BluetoothDevice>()
    // endregion

    // 添加以下变量
    private var autoConnectEnabled = true
    private val targetDeviceName = "printer"
    private val sppUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bleServiceUUID = UUID.fromString("0000FF00-0000-1000-8000-00805F9B34FB") // 示例UUID，需替换实际值
    private var bleWriteCharUUID = UUID.fromString("0000FF01-0000-1000-8000-00805F9B34FB") // 示例UUID，需替换实际值


    // region 回调接口
    interface BluetoothEventListener {
        fun onBluetoothStateChanged(enabled: Boolean)
        fun onDeviceDiscovered(device: BluetoothDevice, isBle: Boolean)
        fun onDeviceBondStateChanged(device: BluetoothDevice, state: Int)
        fun onBleConnected(device: BluetoothDevice)
        fun onSppConnected(device: BluetoothDevice)
        fun onConnectionFailed(device: BluetoothDevice?, error: String)
        fun onDataReceived(data: ByteArray, isBle: Boolean)
    }

    private val listeners = mutableSetOf<BluetoothEventListener>()

    fun addListener(listener: BluetoothEventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: BluetoothEventListener) {
        listeners.remove(listener)
    }
    // endregion


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    val isEnabled = state == BluetoothAdapter.STATE_ON
                    notifyBluetoothState(isEnabled)
                }

                BluetoothDevice.ACTION_FOUND -> {
                    handleDeviceFound(intent)
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    handleBondStateChanged(intent)
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Bluetooth", "Discovery finished")
                }
            }
        }
    }

    init {
        registerReceivers()
        startAutoConnect()
    }

    // 添加自动连接逻辑
    private fun startAutoConnect() {
        if (!isBluetoothEnabled()) {
            notifyError("Bluetooth not enabled")
            return
        }

        // 先检查已配对设备
        getPairedDevices().firstOrNull { it.name?.contains(targetDeviceName, true) == true }?.let {
            connectBle(it)
            return
        }

        // 没有已配对设备则开始扫描
        startDiscovery()
    }
    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)
    }

    fun release() {
        context.unregisterReceiver(receiver)
        disconnectBle()
        disconnectSpp()
        executor.shutdown()
    }
    // endregion

    // region 核心功能
    // ==============================================================================================

    // 检查蓝牙是否可用
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    // 开启/关闭蓝牙（需要系统权限）
    @SuppressLint("MissingPermission")
    fun setBluetoothEnabled(enable: Boolean) {
        if (enable) {
            bluetoothAdapter?.enable()
        } else {
            bluetoothAdapter?.disable()
        }
    }

    // 开始扫描设备（自动区分 BLE 和经典蓝牙）
//    @SuppressLint("MissingPermission")
//    fun startDiscovery() {
//        if (!checkPermissions()) {
//            notifyError("Missing Bluetooth permissions")
//            return
//        }
//
//        discoveredDevices.clear()
//
//        // 扫描经典蓝牙设备
//        bluetoothAdapter?.startDiscovery()
//
//        // 扫描 BLE 设备
//        bleScanner = bluetoothAdapter?.bluetoothLeScanner
//        bleScanner?.startScan(bleScanCallback)
//    }

    // 修改启动扫描方法
    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if (!checkPermissions()) return

        // 清空设备列表
        discoveredDevices.clear()

        // 设置扫描参数（优化BLE扫描）
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val filters = listOf(
            ScanFilter.Builder()
                .setDeviceName(targetDeviceName)
                .build()
        )

        // 启动扫描
        bluetoothAdapter?.startDiscovery()
        bleScanner?.startScan(filters, settings, bleScanCallback)
    }


    // 停止扫描
    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
        bleScanner?.stopScan(bleScanCallback)
    }

    // 获取已配对设备
    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    // 配对设备（经典蓝牙）
    @SuppressLint("MissingPermission")
    fun pairDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            device.createBond()
        }
    }

    // 取消配对（需要系统权限）
    @SuppressLint("MissingPermission")
    fun unpairDevice(device: BluetoothDevice) {
        try {
            val method: Method = device.javaClass.getMethod("removeBond")
            method.invoke(device)
        } catch (e: Exception) {
            notifyError("Unpair failed: ${e.message}")
        }
    }

    // 连接 BLE 设备
//    fun connectBle(device: BluetoothDevice, serviceUUID: UUID, writeCharUUID: UUID) {
//        disconnectBle()
//        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
//            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    gatt.discoverServices()
//                } else {
//                    notifyConnectionFailed(device, "BLE connection lost")
//                }
//            }
//
//            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
//                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    val service = gatt.getService(serviceUUID)
//                    writeCharacteristic = service.getCharacteristic(writeCharUUID)
//                    enableNotifications(gatt, service.getCharacteristic(writeCharUUID))
//                    notifyBleConnected(device)
//                }
//            }
//
//            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
//                notifyDataReceived(characteristic.value, true)
//            }
//        }, BluetoothDevice.TRANSPORT_LE)
//    }

    fun connectBle(device: BluetoothDevice) {
        disconnectBle()
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else {
                    notifyConnectionFailed(device, "BLE connection failed: $status")
                    startAutoConnect() // 失败后重新尝试
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val service = gatt.getService(bleServiceUUID)
                    writeCharacteristic = service.getCharacteristic(bleWriteCharUUID)

                    // 启用通知
                    enableNotifications(gatt, service.getCharacteristic(bleWriteCharUUID))
                    notifyBleConnected(device)
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                notifyDataReceived(characteristic.value, true)
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    notifyError("Write failed: $status")
                }
            }
        }, BluetoothDevice.TRANSPORT_LE)
    }

    // 连接 SPP 设备
    fun connectSpp(device: BluetoothDevice) {
        executor.execute {
            try {
                disconnectSpp()
                sppSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                sppSocket?.connect()
                sppInputStream = sppSocket?.inputStream
                sppOutputStream = sppSocket?.outputStream
                notifySppConnected(device)
                startSppDataMonitoring()
            } catch (e: IOException) {
                notifyConnectionFailed(device, "SPP connection failed: ${e.message}")
            }
        }
    }

    // 发送数据
    fun sendData(data: ByteArray, isBle: Boolean) {
        if (isBle) {
            writeCharacteristic?.value = data
            bluetoothGatt?.writeCharacteristic(writeCharacteristic)
        } else {
            executor.execute {
                try {
                    sppOutputStream?.write(data)
                    sppOutputStream?.flush()
                } catch (e: IOException) {
                    notifyError("SPP write failed: ${e.message}")
                }
            }
        }
    }

    // 断开连接
    @SuppressLint("MissingPermission")
    fun disconnectBle() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    fun disconnectSpp() {
        try {
            sppSocket?.close()
        } catch (e: IOException) {
            Log.e("Bluetooth", "SPP close error", e)
        }
        sppSocket = null
    }
    // endregion

    // region 私有方法
    // ==============================================================================================

    private fun checkPermissions(): Boolean {
        val required = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        return required.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?) {
        characteristic?.let {
            gatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }

    private fun startSppDataMonitoring() {
        executor.execute {
            val buffer = ByteArray(1024)
            while (sppSocket?.isConnected == true) {
                try {
                    val bytes = sppInputStream?.read(buffer) ?: -1
                    if (bytes > 0) {
                        notifyDataReceived(buffer.copyOf(bytes), false)
                    }
                } catch (e: IOException) {
                    notifyError("SPP read error: ${e.message}")
                    disconnectSpp()
                    break
                }
            }
        }
    }

    // 添加配置方法（可选）
    fun configureBleParams(serviceUUID: UUID, writeCharUUID: UUID) {
        this.bleServiceUUID = serviceUUID
        this.bleWriteCharUUID = writeCharUUID
    }

    // endregion

    // region 通知方法
    // ==============================================================================================

    private fun notifyBluetoothState(enabled: Boolean) {

        mainHandler.post {
            listeners.forEach { it.onBluetoothStateChanged(enabled) }
        }
    }

    private fun notifyDeviceDiscovered(device: BluetoothDevice, isBle: Boolean) {
        mainHandler.post {
            listeners.forEach { it.onDeviceDiscovered(device, isBle) }
        }
    }

    private fun notifyBondStateChanged(device: BluetoothDevice, state: Int) {
        mainHandler.post {
            listeners.forEach { it.onDeviceBondStateChanged(device, state) }
        }
    }

    private fun notifyBleConnected(device: BluetoothDevice) {
        mainHandler.post {
            listeners.forEach { it.onBleConnected(device) }
        }
    }

    private fun notifySppConnected(device: BluetoothDevice) {
        mainHandler.post {
            listeners.forEach { it.onSppConnected(device) }
        }
    }

    private fun notifyConnectionFailed(device: BluetoothDevice, error: String) {
        mainHandler.post {
            listeners.forEach { it.onConnectionFailed(device, error) }
        }
    }

    private fun notifyDataReceived(data: ByteArray, isBle: Boolean) {
        mainHandler.post {
            listeners.forEach { it.onDataReceived(data, isBle) }
        }
    }

    private fun notifyError(message: String) {
        mainHandler.post {
            listeners.forEach { it.onConnectionFailed(null, message) }
        }
    }

    // BLE 扫描回调
//    private val bleScanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            result.device?.let {
//                if (!discoveredDevices.containsKey(it.address)) {
//                    discoveredDevices[it.address] = it
//                    notifyDeviceDiscovered(it, true)
//                }
//            }
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            notifyError("BLE scan failed: $errorCode")
//        }
//    }

    // 修改扫描回调
    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.device?.let { device ->
                if (device.name?.contains(targetDeviceName, true) == true) {
                    handleTargetDeviceFound(device)
                }
            }
        }
    }

    // 新增目标设备处理
    @SuppressLint("MissingPermission")
    private fun handleTargetDeviceFound(device: BluetoothDevice) {
        if (!autoConnectEnabled) return

        stopDiscovery()
        discoveredDevices.clear()

        // 优先尝试BLE连接
        if (device.type == BluetoothDevice.DEVICE_TYPE_LE ||
            device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
            connectBle(device)
        } else {
            notifyError("Found target device but not BLE")
        }
    }

    // 设备发现处理
    @SuppressLint("MissingPermission")
    private fun handleDeviceFound(intent: Intent) {
        (intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))?.let { device ->
            if (!discoveredDevices.containsKey(device.address)) {
                discoveredDevices[device.address] = device
                val isBle = device.type == BluetoothDevice.DEVICE_TYPE_LE ||
                        device.type == BluetoothDevice.DEVICE_TYPE_DUAL
                notifyDeviceDiscovered(device, isBle)
            }
        }
    }

    // 配对状态处理
    private fun handleBondStateChanged(intent: Intent) {
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
        device?.let { notifyBondStateChanged(it, state) }
    }
}