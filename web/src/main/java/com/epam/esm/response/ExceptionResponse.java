package com.epam.esm.response;

public class ExceptionResponse extends Response {
    private String errorCode;

    public ExceptionResponse(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
