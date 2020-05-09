package com.hd.im.dto;

public class Result {
    private int    errorCode = 0;
    private String errorMsg  = "success";
    private Object data;

    public static Result ok(Object data) {
        return new Result(data);
    }

    public static Result fail(int errorCode, String errorMsg) {
        return new Result(errorCode, errorMsg);
    }

    public Result() {
    }

    public Result(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public Result(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
