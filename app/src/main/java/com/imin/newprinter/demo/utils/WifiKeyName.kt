package com.imin.newprinter.demo.utils

/**
 * @Author: hy
 * @date: 2025/5/6
 * @description:
 */
object WifiKeyName {
    const val WIFI_SETUP_NET_ALL = "wifi_setup_net_all"//无线打印机配网

    const val WIFI_SETUP_NET = "wifi_setup_net"//无线打印机配网

    const val WIRELESS_CONNECT_TYPE = "wireless_connect_type" //设置打印机连接类型usb/wifi/bt
    const val WIFI_CONNECT_IP = "wifi_connect_ip" //通过IP连接WiFi打印机
    const val WIFI_DHCP = "wifi_dhcp" //设置DHCP模式

    const val BT_CONNECT_MAC = "bt_connect_mac" //通过Mac连接BT打印机

    const val WIFI_DISCONNECT = "wifi_disconnect" //断开WIFI连接
    const val BT_DISCONNECT = "bt_disconnect" //断开BT连接
    const val WIFI_CURRENT_CONNECT_IP = "wifi_current_connect_ip" //查询当前连接WIF IP
    const val BT_CURRENT_CONNECT_MAC = "bt_current_connect_mac" //查询当前连接BT Mac
    const val WIFI_ALL_CONNECT_IP = "wifi_all_connect_ip" //查询已连接的WiFi IP

    const val WIRELESS_CONNECT_STATUS = "wireless_connect_status" //查询无线打印机连接状态
    const val WIFI_IP = "wifi_ip" //查询IP

}