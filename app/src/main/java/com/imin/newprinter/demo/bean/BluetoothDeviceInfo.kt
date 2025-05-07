package com.imin.newprinter.demo.bean

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */
data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val rssiBle: Int,
    val rssiWifi: Int,
    val wifiConnectStatus: Int,//0 未连接  1连网  2 蓝牙  3 wifi+蓝牙
    val printerWorkingStatus: Int,
    val ioStatus: String,
    val ipAddress: String,
    val wifiName: String
)
