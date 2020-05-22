package com.hd.im.netty.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.im.core.proto.HDIMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: IMServerProtocolDecoderV2
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/21 9:11
 * @Version: 1.0.0
 **/
public class IMServerProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    private Logger logger = LoggerFactory.getLogger(IMServerProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 0) {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            HDIMProtocol.MessagePack messagePack = null;
            try {
                messagePack = HDIMProtocol.MessagePack.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                logger.error("the message pack parse fail.", e);
                return;
            }
            int                    commandNumber = messagePack.getCommand();
            HDIMProtocol.IMCommand command       = HDIMProtocol.IMCommand.forNumber(commandNumber);
            if (command == null || command.getNumber() == HDIMProtocol.IMCommand.RESERVED_VALUE) {
                logger.error("the command not support. {}", command);
                return;
            }
            list.add(messagePack);
        }
    }
}
