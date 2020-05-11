package com.hd.im.commons.proto;

/**
 * @ClassName: Tester
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/11 14:53
 * @Version: 1.0.0
 **/
public class Tester {
    public static void main(String[] args) {
        HDIMProtocol.ClientInfo build = HDIMProtocol.ClientInfo.newBuilder().setAppVersion("1.0.0").setPlatForm(0).setClientId("cid:123").setUserId("admin").build();
        System.out.println(build);
    }
}
