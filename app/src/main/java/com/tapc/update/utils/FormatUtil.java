package com.tapc.update.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Administrator on 2017/3/21.
 */

public class FormatUtil {
    public static double formatDouble(double value) {
        BigDecimal bg = new BigDecimal(value).setScale(1, RoundingMode.HALF_UP);
        return bg.doubleValue();
    }

    public static double formatDouble(int bit, double value) {
        BigDecimal bg = new BigDecimal(value).setScale(bit, RoundingMode.HALF_UP);
        return bg.doubleValue();
    }

}
