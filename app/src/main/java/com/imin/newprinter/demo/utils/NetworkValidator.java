package com.imin.newprinter.demo.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class NetworkValidator {
    // IP 地址（禁止前导零）‌:ml-citation{ref="7" data="citationList"}
    private static final String IP_REGEX =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    // 子网掩码（点分十进制或 CIDR）‌:ml-citation{ref="3,7" data="citationList"}
    private static final String MASK_REGEX =
            "^(255\\.(255\\.(255\\.(255|254|252|248|240|224|192|128|0)|...))|/([0-9]|[1-2][0-9]|3[0-2]))$";

    // 域名格式（兼容国际化域名）‌:ml-citation{ref="4" data="citationList"}
    private static final String DOMAIN_REGEX =
            "^([a-zA-Z0-9\\-\\u4e00-\\u9fa5]+\\.)+[a-zA-Z\\u4e00-\\u9fa5]{2,}$";

    public static boolean validateIP(String ip) {
        return Pattern.matches(IP_REGEX, ip);  // IP 格式校验‌:ml-citation{ref="7" data="citationList"}
    }

    public static boolean validateMask(String mask) {
        // 处理 CIDR 格式转换
        if (mask.startsWith("/")) {
            int cidr = Integer.parseInt(mask.substring(1));
            return cidr >= 0 && cidr <= 32;  // CIDR 范围校验‌:ml-citation{ref="3" data="citationList"}
        }
        return Pattern.matches(MASK_REGEX, mask);  // 点分格式校验‌:ml-citation{ref="3,7" data="citationList"}
    }

    public static boolean validateGateway(String ip, String gw, String mask) {
        if (!validateIP(gw)) return false;

        try {
            // 计算子网地址对比‌:ml-citation{ref="3" data="citationList"}
            InetAddress ipAddr = InetAddress.getByName(ip);
            InetAddress gwAddr = InetAddress.getByName(gw);
            byte[] ipBytes = ipAddr.getAddress();
            byte[] gwBytes = gwAddr.getAddress();
            byte[] maskBytes = InetAddress.getByName(mask).getAddress();

            for (int i = 0; i < ipBytes.length; i++) {
                if ((ipBytes[i] & maskBytes[i]) != (gwBytes[i] & maskBytes[i])) {
                    return false;
                }
            }
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean validateDNS(String dns) {
        String[] servers = dns.split(",");
        for (String server : servers) {
            String trimmed = server.trim();
            // 混合校验 IP/域名格式‌:ml-citation{ref="4" data="citationList"}
            if (!Pattern.matches(IP_REGEX, trimmed) &&
                    !Pattern.matches(DOMAIN_REGEX, trimmed)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidMac(String mac) {
        String pattern = "^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$";
        return mac.matches(pattern);
    }

}

