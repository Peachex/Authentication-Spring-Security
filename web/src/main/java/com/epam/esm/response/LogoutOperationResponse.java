package com.epam.esm.response;

import com.epam.esm.constant.PropertyFileName;

import java.util.Locale;
import java.util.ResourceBundle;

public class LogoutOperationResponse extends Response {
    private final String operation;
    private final Locale responseLocale;

    public LogoutOperationResponse(EntityOperationResponse.Operation operation, String messageKey, Locale locale) {
        super(ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                locale).getString(messageKey));
        this.responseLocale = locale;
        this.operation = operation.getLocalizedOperationName(locale);
    }

    public String getOperation() {
        return operation;
    }

    public Locale getResponseLocale() {
        return responseLocale;
    }
}
