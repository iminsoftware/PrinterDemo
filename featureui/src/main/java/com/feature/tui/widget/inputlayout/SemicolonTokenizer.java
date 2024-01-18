package com.feature.tui.widget.inputlayout;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

/**
 * @Author: zhongxing
 * @Date: 2021/7/16 8:45
 * @Description:
 */
public class SemicolonTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private char charS;

    private String mSTring;

    public SemicolonTokenizer(char charS) {
        this.charS = charS;
        mSTring = String.valueOf(charS);
    }

    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && text.charAt(i - 1) != charS) {
            i--;
        }

        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == charS) {
                return i;
            } else {
                i++;
            }
        }
        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == charS) {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + mSTring);
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
                return sp;
            } else {
                return text + mSTring;
            }
        }
    }

}
