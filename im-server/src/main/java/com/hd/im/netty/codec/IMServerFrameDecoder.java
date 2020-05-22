package com.hd.im.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @ClassName: IMServerFrameDecoder
 * @Description: 消息修整
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:39:44
 */
public class IMServerFrameDecoder extends LengthFieldBasedFrameDecoder {

    private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 261120; /* 255KB */

    public IMServerFrameDecoder() {
        super(DEFAULT_MAX_BYTES_IN_MESSAGE, 0, 4, 0, 4);
    }
}
