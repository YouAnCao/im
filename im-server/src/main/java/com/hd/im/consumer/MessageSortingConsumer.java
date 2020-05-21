package com.hd.im.consumer;

import com.hd.im.context.ApplicationContextHolder;
import com.hd.im.service.MessageService;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.PublishMessage;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.GSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: MessageSortingConsumer
 * @Description: 消息分发消费
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 15:10
 * @Version: 1.0.0
 **/
public class MessageSortingConsumer {

    private static Logger logger = LoggerFactory.getLogger(MessageSortingConsumer.class);

    private static final int processors = Runtime.getRuntime().availableProcessors() * 2;

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), (r) -> {
        Thread thread = new Thread(r);
        thread.setName("message-sorting-thread:" + r.hashCode());
        return thread;
    });

    public static void starter() {
        MessageSortingConsumer.init(() -> {
            while (true) {
                try {
                    List<String> data = RedisStandalone.REDIS.brpop(10000, RedisConstants.MESSAGE_SORTING);
                    if (data != null && data.size() > 0) {
                        String               val            = data.get(1);
                        PublishMessage       publishMessage = GSONParser.getInstance().fromJson(val, PublishMessage.class);
                        HDIMProtocol.Message message        = publishMessage.toMessage();
                        MessageService       messageService = ApplicationContextHolder.getApplicationContext().getBean("messageServiceImpl", MessageService.class);
                        messageService.sendMessage(publishMessage);
                    } else {
                        logger.info("message sorting heartbeat. {}", Thread.currentThread().getId());
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
    }

    private static void init(Runnable exec) {
        for (int i = 0; i < processors; i++) {
            executor.execute(exec);
        }
    }
}
