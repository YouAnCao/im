package com.hd.im.handler;

import com.hd.im.handler.annotation.Handler;
import com.im.core.proto.HDIMProtocol;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: HandlerContext
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 11:03
 * @Version: 1.0.0
 **/
@Component
public class HandlerContext {

    private static Map<HDIMProtocol.PublishType, IMHandler> handlers = new HashMap<>();

    public static void registryAll(ApplicationContext context) {
        Map<String, IMHandler> beans = context.getBeansOfType(IMHandler.class);
        if (beans == null || beans.size() == 0) {
            return;
        }
        beans.forEach((k, v) -> {
            Handler annotation = context.findAnnotationOnBean(k, Handler.class);
            handlers.put(annotation.value(), v);
        });
    }

    public static IMHandler getHandler(HDIMProtocol.PublishType publishType) {
        return handlers.get(publishType);
    }
}
