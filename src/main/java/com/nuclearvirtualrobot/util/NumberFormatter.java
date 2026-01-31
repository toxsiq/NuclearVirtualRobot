package com.nuclearvirtualrobot.util;

import java.text.DecimalFormat;

public final class NumberFormatter {
    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "OC", "N", "D"
    };

    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("0.0");

    private NumberFormatter() {
    }

    public static String format(double value) {
        if (value < 1000.0) {
            return String.format("%.0f", value).replace(",", ".");
        }
        int magnitude = 0;
        double scaled = value;
        while (scaled >= 1000.0 && magnitude < SUFFIXES.length - 1) {
            scaled /= 1000.0;
            magnitude++;
        }
        return ONE_DECIMAL.format(scaled).replace(",", ".") + SUFFIXES[magnitude];
    }
}
