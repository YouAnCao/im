package com.hd.im.commons.constants;

import com.hd.im.commons.proto.HDIMProtocol;

public enum DecoderError {
    LOGIN(HDIMProtocol.HeadType.LOGIN.getNumber(),6),
    PUBLISH(HDIMProtocol.HeadType.PUBLISH.getNumber(), 7);

    private int errorCode;
    private int operational;

    DecoderError(int operational,int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getOperational() {
        return operational;
    }
}
