package com.im.core.proto;

import com.im.core.entity.PublishMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName: Tester
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 18:03
 * @Version: 1.0.0
 **/
public class Tester {
    public static void main(String[] args) {
        HDIMProtocol.MessagePack build = HDIMProtocol.MessagePack.newBuilder().setCommand(HDIMProtocol.IMCommand.LOGIN_VALUE).build();
        System.out.println(build.getSerializedSize());
        System.out.println(build.toByteArray().length);
    }
}
