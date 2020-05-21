//package com.hd.im.netty.codec;
//
//import com.im.core.proto.HDIMProtocol;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//
//import java.util.List;
//
///**
// * @ClassName: IMServerProtocolDecoderV2
// * @Description: TODO
// * @Author: Lyon.Cao
// * @Date: 2020/5/21 9:11
// * @Version: 1.0.0
// **/
//public class IMServerProtocolDecoderV2 extends MessageToMessageDecoder<ByteBuf> {
//
//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        if (byteBuf.readableBytes() > 0) {
//            byte[] data = new byte[byteBuf.readableBytes()];
//            byteBuf.readBytes(data);
//            HDIMProtocol.MessagePack messagePack = HDIMProtocol.MessagePack.parseFrom(data);
//            int                      command     = messagePack.getCommand();
//            if (HDIMProtocol.IMCommand.RESERVED_VALUE == command) {
//
//            }
//        } else {
//
//        }
//    }
//}
