package com.hd.im.consumer;

import cn.hutool.core.codec.Base64;
import com.google.protobuf.ByteString;
import com.hd.im.cache.MemorySessionStore;
import com.im.core.constants.RedisConstants;
import com.im.core.entity.NotifyMessage;
import com.im.core.entity.UserSession;
import com.im.core.proto.HDIMProtocol;
import com.im.core.redis.RedisStandalone;
import com.im.core.utils.AESHelper;
import com.im.core.utils.GSONParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: MessageNotifyConsumer
 * @Description: 消息通知消费
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 15:10
 * @Version: 1.0.0
 **/
public class MessageNotifyConsumer {

    private static Logger logger = LoggerFactory.getLogger(MessageNotifyConsumer.class);

    private static final int processors = Runtime.getRuntime().availableProcessors() * 2;

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), (r) -> {
        Thread thread = new Thread(r);
        thread.setName("message-notify-thread:" + r.hashCode());
        return thread;
    });

    public static void starter() {
        MessageNotifyConsumer.init(() -> {
            while (true) {
                try {
                    List<String> data = RedisStandalone.REDIS.brpop(10000, RedisConstants.MESSAGE_NOTIFY);
                    if (data != null && data.size() > 0) {
                        String        val           = data.get(1);
                        NotifyMessage notifyMessage = GSONParser.getInstance().fromJson(val, NotifyMessage.class);

                        String      clientId             = notifyMessage.getClientId();
                        String      userId               = notifyMessage.getUserId();
                        UserSession userSessionOnChannel = MemorySessionStore.getInstance().getUserSessionOnChannel(clientId, userId);
                        if (userSessionOnChannel == null) {
                            logger.error("can not send message, the user session is not found. clientId:{}, userId:{}", clientId, userId);
                            continue;
                        }
                        HDIMProtocol.NotifyMessage notifyMessageBody = notifyMessage.toNotifyMessage();
                        HDIMProtocol.MessagePack   messagePack       = HDIMProtocol.MessagePack.newBuilder().setPayload(ByteString.copyFrom(notifyMessageBody.toByteArray())).setSequenceId(System.currentTimeMillis()).setCommand(HDIMProtocol.IMCommand.MN_VALUE).build();
                        MemorySessionStore.getInstance().sendMessage(clientId, userId, messagePack);
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
