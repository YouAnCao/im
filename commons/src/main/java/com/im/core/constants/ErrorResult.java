package com.im.core.constants;

public class ErrorResult {
    private int op;
    private int ErrorCode;

    public ErrorResult(int op, int errorCode) {
        this.op = op;
        ErrorCode = errorCode;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }
}
