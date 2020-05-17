package com.im.core.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

/**
 * @ClassName: Ms
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 11:06
 * @Version: 1.0.0
 **/
public class TimeUtils {
    public static Long getMicTime() {
        Long currentTime = System.currentTimeMillis();
        Long nanoTime    = System.nanoTime();
        Long nano        = nanoTime - (nanoTime / 1000000 * 1000000);
        return currentTime * 1000 + nano;
    }
}
