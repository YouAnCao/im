package com.hd.im.handler;

import com.hd.im.commons.proto.HDIMProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IMServerPublishHandler extends SimpleChannelInboundHandler<HDIMProtocol.Publish> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HDIMProtocol.Publish publish) throws Exception {
        int publishType = publish.getPublishType();
        switch (publishType) {
            case HDIMProtocol.PublishType.MS_VALUE:
                // TODO 消息推送
                byte[] bytes = publish.getPayload().toByteArray();
                HDIMProtocol.Message message = HDIMProtocol.Message.parseFrom(bytes);
                break;
            case HDIMProtocol.PublishType.MP_VALUE:
                // TODO 消息拉取
                break;
            default:
                break;
        }
    }
}
