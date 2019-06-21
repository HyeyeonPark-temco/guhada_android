package io.temco.guhada.common.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;

import java.text.DecimalFormat;

import io.temco.guhada.common.listener.OnLinkClickListener;

public class TextUtil {

    ////////////////////////////////////////////////
    // Decimal Format
    public static String getDecimalFormat(int value) {
        return new DecimalFormat("#,###").format(value);
    }

    // create Link Text
    public static SpannableStringBuilder createTextWithLink(Context context,
                                                            @StringRes int text,
                                                            @StringRes int link,
                                                            @DimenRes int size,
                                                            boolean addSpace,
                                                            OnLinkClickListener listener) {
        String l = context.getResources().getString(link);
        SpannableStringBuilder sb = new SpannableStringBuilder(l);
        LinkSpan ls = new LinkSpan(context, size, listener);
        sb.setSpan(ls, 0, l.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        if (addSpace) {
            return new SpannableStringBuilder(context.getResources().getString(text)).append(" ").append(sb);
        } else {
            return new SpannableStringBuilder(context.getResources().getString(text)).append(sb);
        }
    }

    ////////////////////////////////////////////////
}