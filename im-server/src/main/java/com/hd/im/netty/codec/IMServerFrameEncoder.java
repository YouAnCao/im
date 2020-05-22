package com.hd.im.netty.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @Description: 响应编码
 * @Author: Lyon.Cao
 * @Date: 2020/5/10 23:14
 * @Version: 1.0.0
 **/
public class IMServerFrameEncoder extends LengthFieldPrepender {
    public IMServerFrameEncoder() {
        super(4);
    }
}
