package com.gsd.kolorbi.model;

public class APIResponse {

    int status;
    String message;
    Object data;

    public final static int SUCCESS_STATUS = 0;
    public final static int FAIL_STATUS = 99;

    public final static String SUCCESS_MESSAGE = "Request successfully executed";
    public final static String FAILED_MESSAGE = "Unable to execute request";

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
