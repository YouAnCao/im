package com.hd.im.context;

import com.hd.im.handler.HandlerContext;
import org.springframework.context.ApplicationContext;

public class ApplicationContextHolder {

    private static ApplicationContext applicationContext;


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void init() {
        HandlerContext.registryAll(applicationContext);
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
        init();
    }
}
