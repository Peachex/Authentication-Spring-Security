package com.epam.esm.exception;

import com.epam.esm.constant.PropertyFileName;

import java.util.Locale;
import java.util.ResourceBundle;

public class JwtAuthenticationException extends RuntimeException {
    private String errorCode;
    private String messageKey;

    public JwtAuthenticationException(String errorCode, String messageKey) {
        this.errorCode = errorCode;
        this.messageKey = messageKey;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getLocalizedMessage(Locale locale) {
        return ResourceBundle.getBundle(PropertyFileName.ERROR_MESSAGES, locale).getString(messageKey);
    }
}
