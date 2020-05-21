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
        Long currentTime = System.currentTimeMillis() * 1000;
        Long nanoTime    = System.nanoTime();
        return currentTime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }

    public static void main(String[] args) {
        long pre = 0L;
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                pre = getMicTime();
            } else {
                System.out.print((getMicTime() > pre) ? 1 : 0);
            }
        }
    }
}
