package com.zyuco.maskbook.model;

public class ErrorResponse {
    private int errno;
    private String message;

    public ErrorResponseCode getErrno() {
        return ErrorResponseCode.fromInt(errno);
    }

    public String getMessage() {
        return message;
    }
}
