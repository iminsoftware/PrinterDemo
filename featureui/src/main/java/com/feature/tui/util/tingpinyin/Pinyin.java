package com.feature.tui.util.tingpinyin;

public class Pinyin {
    private Pinyin() {
    }

    public static String toPinyin(char c) {
        if (isChinese(c)) {
            return c == 12295 ? "LING" : PinyinData.PINYIN_TABLE[getPinyinCode(c)];
        } else {
            return String.valueOf(c);
        }
    }

    public static boolean isChinese(char c) {
        return 19968 <= c && c <= '龥' && getPinyinCode(c) > 0 || 12295 == c;
    }

    private static int getPinyinCode(char c) {
        int offset = c - 19968;
        if (offset >= 0 && offset < 7000) {
            return decodeIndex(PinyinCode1.PINYIN_CODE_PADDING, PinyinCode1.PINYIN_CODE, offset);
        } else {
            return 7000 <= offset && offset < 14000 ? decodeIndex(PinyinCode2.PINYIN_CODE_PADDING, PinyinCode2.PINYIN_CODE, offset - 7000) : decodeIndex(PinyinCode3.PINYIN_CODE_PADDING, PinyinCode3.PINYIN_CODE, offset - 14000);
        }
    }

    private static short decodeIndex(byte[] paddings, byte[] indexes, int offset) {
        int index1 = offset / 8;
        int index2 = offset % 8;
        short realIndex = (short) (indexes[offset] & 255);
        if ((paddings[index1] & PinyinData.BIT_MASKS[index2]) != 0) {
            realIndex = (short) (realIndex | 256);
        }

        return realIndex;
    }
}