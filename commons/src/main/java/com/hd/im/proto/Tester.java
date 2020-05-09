package com.hd.im.proto;

import com.google.protobuf.ByteString;

public class Tester {
	public static void main(String[] args) {
		HDIMProtocol.Login login = HDIMProtocol.Login.newBuilder().setToken("token")
				.setEncryptData(ByteString.copyFrom("encrypt".getBytes()))
				.setEncryptKey(ByteString.copyFrom("key".getBytes())).build();
		System.out.println(login);
	}
}
