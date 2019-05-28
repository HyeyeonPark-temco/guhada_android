package io.temco.guhada.common.util;

import java.text.DecimalFormat;

public class TextUtil {

    ////////////////////////////////////////////////
    // Decimal Format
    public static String getDecimalFormat(int value) {
        return new DecimalFormat("#,###").format(value);
    }

    ////////////////////////////////////////////////
}