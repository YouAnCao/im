package com.hd.im;

import com.hd.im.consumer.MessageNotifyConsumer;
import com.hd.im.consumer.MessageSortingConsumer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @ClassName: Applistener
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/18 10:13
 * @Version: 1.0.0
 **/
@Service
public class IMServerApplicationListener implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        MessageSortingConsumer.starter();
        MessageNotifyConsumer.starter();
    }
}
