package com.imin.newprinterdemo.command;

import com.imin.newprinterdemo.utils.ConvertUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class Command {

    private int responseTimeout = 500;

    public abstract byte[] getCommand();


    public byte[] toBytes(Vector<Byte> src) {
        if (src == null || src.size() < 1) {
            return new byte[0];
        }
        byte[] dst = new byte[src.size()];
        for (int i = 0; i < src.size(); i++) {
            dst[i] = src.get(i).byteValue();
        }
        return dst;
    }

    public String toString() {
        return ConvertUtils.bytesToHex(getCommand());
    }

    protected static Map<String, String> getResponseMap(byte[] data) {
        if (data == null || data.length < 1) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String dat = new String(data);
        String[] split = dat.split(";");
        for (String s : split) {
            if (!s.isEmpty()) {
                String[] sub = s.split(":");
                if (sub.length >= 2) {
                    map.put(sub[0], sub[1]);
                }
            }
        }
        return map;
    }

    public int getResponseTimeout() {
        return this.responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {
        if (responseTimeout > 0) {
            this.responseTimeout = responseTimeout;
        }
    }
}
