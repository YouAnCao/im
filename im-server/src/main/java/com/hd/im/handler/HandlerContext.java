package com.hd.im.handler;

import com.hd.im.handler.annotation.Handler;
import com.im.core.proto.HDIMProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: HandlerContext
 * @Description: 业务处理类
 * @Author: Lyon.Cao
 * @Date: 2020/5/15 11:03
 * @Version: 1.0.0
 **/
@Component
public class HandlerContext {

    private static Logger logger = LoggerFactory.getLogger(HandlerContext.class);

    private static Map<HDIMProtocol.IMCommand, IMHandler> handlers = new HashMap<>();

    public static void registryAll(ApplicationContext context) {
        Map<String, IMHandler> beans = context.getBeansOfType(IMHandler.class);
        if (beans == null || beans.size() == 0) {
            return;
        }
        beans.forEach((k, v) -> {
            Handler annotation = context.findAnnotationOnBean(k, Handler.class);
            logger.info("handler {} has registry.", annotation.value().name());
            handlers.put(annotation.value(), v);
        });
    }

    public static IMHandler getHandler(HDIMProtocol.IMCommand command) {
        return handlers.get(command);
    }
}
