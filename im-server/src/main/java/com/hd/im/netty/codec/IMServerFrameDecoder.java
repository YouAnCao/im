package com.hd.im.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @ClassName: IMServerFrameDecoder
 * @Description: 消息修整
 * @author: Lyon.Cao
 * @date 2020年5月10日 上午1:39:44
 */
public class IMServerFrameDecoder extends LengthFieldBasedFrameDecoder {

    public IMServerFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
