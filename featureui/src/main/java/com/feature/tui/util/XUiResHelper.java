package com.feature.tui.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.feature.tui.R;

/**
 * @author mark
 * Time: 2020/12/16 09:30
 * Description: 资源文件的工具
 */
public class XUiResHelper {
    private static TypedValue sTmpValue;

    public static float getAttrFloatValue(Context context, int attr) {
        return getAttrFloatValue(context.getTheme(), attr);
    }

    public static float getAttrFloatValue(Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        return (!theme.resolveAttribute(attr, sTmpValue, true)) ? 0f : sTmpValue.getFloat();
    }

    public static int getAttrColor(Context context, int attrRes) {
        return getAttrColor(context.getTheme(), attrRes);
    }

    public static int getAttrColor(Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        if (!theme.resolveAttribute(attr, sTmpValue, true)) {
            return 0;
        }
        return sTmpValue.type == TypedValue.TYPE_ATTRIBUTE ? getAttrColor(theme, sTmpValue.data) : sTmpValue.data;
    }

    public static ColorStateList getAttrColorStateList(Context context, int attrRes) {
        return getAttrColorStateList(context, context.getTheme(), attrRes);
    }

    public static ColorStateList getAttrColorStateList(Context context, Resources.Theme theme, int attr) {
        ColorStateList colorStateList;
        if (attr == 0) {
            return null;
        }
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        if (!theme.resolveAttribute(attr, sTmpValue, true)) {
            return null;
        }
        if (sTmpValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && sTmpValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return ColorStateList.valueOf(sTmpValue.data);
        }
        if (sTmpValue.type == TypedValue.TYPE_ATTRIBUTE) {
            return getAttrColorStateList(context, theme, sTmpValue.data);
        }
        return sTmpValue.resourceId == 0 ? null : ContextCompat.getColorStateList(
                context,
                sTmpValue.resourceId
        );
    }

    public static Drawable getAttrDrawable(Context context, int attr) {
        return getAttrDrawable(context, context.getTheme(), attr);
    }

    public static Drawable getAttrDrawable(Context context, Resources.Theme theme, int attr) {
        if (attr == 0) {
            return null;
        }
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        if (!theme.resolveAttribute(attr, sTmpValue, true)) {
            return null;
        }
        if (sTmpValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && sTmpValue.type <= TypedValue.TYPE_LAST_COLOR_INT
        ) {
            return new ColorDrawable(sTmpValue.data);
        }
        if (sTmpValue.type == TypedValue.TYPE_ATTRIBUTE) {
            return getAttrDrawable(context, theme, sTmpValue.data);
        }
        return sTmpValue.resourceId != 0 ?
                AppCompatResources.getDrawable(context, sTmpValue.resourceId) : null;
    }

    public static Drawable getAttrDrawable(Context context, TypedArray typedArray, int index) {
        TypedValue value = typedArray.peekValue(index);
        if (value != null && value.type != TypedValue.TYPE_ATTRIBUTE && value.resourceId != 0) {
            return AppCompatResources.getDrawable(context, value.resourceId);
        }
        return null;
    }

    public static int getAttrDimen(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        return (!context.getTheme().resolveAttribute(attrRes, sTmpValue, true) ? 0 :
                TypedValue.complexToDimensionPixelSize(sTmpValue.data, context.getResources().getDisplayMetrics()));
    }

    public static String getAttrString(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        if (!context.getTheme().resolveAttribute(attrRes, sTmpValue, true)) {
            return null;
        }
        CharSequence charSequence = sTmpValue.string;
        return charSequence != null ? charSequence.toString() : null;
    }

    public static int getAttrInt(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        context.getTheme().resolveAttribute(attrRes, sTmpValue, true);
        return sTmpValue.data;
    }

    public static void assignTextViewWithAttr(TextView textView, int attrRes) {
        TypedArray typedArray = textView.getContext().obtainStyledAttributes(null, R.styleable.XUITextCommonStyle, attrRes, 0);
        TypedArray a = typedArray;
        int count = a.getIndexCount();
        int paddingLeft = textView.getPaddingLeft();
        int paddingRight = textView.getPaddingRight();
        int paddingTop = textView.getPaddingTop();
        int paddingBottom = textView.getPaddingBottom();
        int n = 0;
        while (n < count) {
            int attr = a.getIndex(n);
            if (attr == R.styleable.XUITextCommonStyle_android_gravity) {
                textView.setGravity(a.getInt(attr, -1));
            } else if (attr == R.styleable.XUITextCommonStyle_android_textColor) {
                textView.setTextColor(a.getColorStateList(attr));
            } else if (attr == R.styleable.XUITextCommonStyle_android_textSize) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(attr, 0));
            } else if (attr == R.styleable.XUITextCommonStyle_android_paddingLeft) {
                paddingLeft = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.XUITextCommonStyle_android_paddingRight) {
                paddingRight = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.XUITextCommonStyle_android_paddingTop) {
                paddingTop = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.XUITextCommonStyle_android_paddingBottom) {
                paddingBottom = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.XUITextCommonStyle_android_singleLine) {
                textView.setSingleLine(a.getBoolean(attr, false));
            } else if (attr == R.styleable.XUITextCommonStyle_android_ellipsize) {
                switch (a.getInt(attr, 3)) {
                    case 1: {
                        textView.setEllipsize(TextUtils.TruncateAt.START);
                        break;
                    }
                    case 2: {
                        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        break;
                    }
                    case 3: {
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                        break;
                    }
                    case 4: {
                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        break;
                    }
                }
            } else if (attr == R.styleable.XUITextCommonStyle_android_maxLines) {
                textView.setMaxLines(a.getInt(attr, -1));
            } else if (attr == R.styleable.XUITextCommonStyle_android_background) {
                textView.setBackground(a.getDrawable(attr));
            } else if (attr == R.styleable.XUITextCommonStyle_android_lineSpacingExtra) {
                textView.setLineSpacing((float) a.getDimensionPixelSize(attr, 0), 1.0f);
            } else if (attr == R.styleable.XUITextCommonStyle_android_drawableLeft) {
                textView.setCompoundDrawablesWithIntrinsicBounds(a.getDrawable(attr), null, null, null);
            } else if (attr == R.styleable.XUITextCommonStyle_android_drawableRight) {
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, a.getDrawable(attr), null);
            } else if (attr == R.styleable.XUITextCommonStyle_android_drawableTop) {
                textView.setCompoundDrawablesWithIntrinsicBounds(null, a.getDrawable(attr), null, null);
            } else if (attr == R.styleable.XUITextCommonStyle_android_drawableBottom) {
                textView.setCompoundDrawablesWithIntrinsicBounds(a.getDrawable(attr), null, null, a.getDrawable(attr));
            } else if (attr == R.styleable.XUITextCommonStyle_android_drawablePadding) {
                textView.setCompoundDrawablePadding(a.getDimensionPixelSize(attr, 0));
            } else if (attr == R.styleable.XUITextCommonStyle_android_textColorHint) {
                textView.setHintTextColor(a.getColor(attr, 0));
            } else if (attr == R.styleable.XUITextCommonStyle_android_textStyle) {
                int styleIndex = a.getInt(attr, -1);
                textView.setTypeface(null, styleIndex);
            } else if (attr == R.styleable.XUITextCommonStyle_android_minHeight) {
                int minHeight = a.getDimensionPixelSize(attr, 0);
                textView.setMinHeight(minHeight);
            }
            ++n;
        }
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        a.recycle();
    }

    public static void hideKeyboard(Context context, View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View v) {
        Object object = context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodManager inputMethodManager = (InputMethodManager) object;
        v.requestFocus();
        inputMethodManager.showSoftInput(v, 0);
    }

}
