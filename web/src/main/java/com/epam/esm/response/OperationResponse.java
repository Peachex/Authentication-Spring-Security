package com.epam.esm.response;

import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.constant.PropertyFileName;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.RepresentationModel;

import java.util.Locale;
import java.util.ResourceBundle;

public class OperationResponse extends RepresentationModel<OperationResponse> {
    private String operation;
    private String message;
    private long objectId;
    private Locale responseLocale;

    public enum Operation {
        CREATION(ResponseMessageName.CREATION_OPERATION),
        DELETION(ResponseMessageName.DELETION_OPERATION),
        UPDATE(ResponseMessageName.UPDATE_OPERATION);

        private final String nameKey;

        Operation(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getLocalizedOperationName(Locale locale) {
            return ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES, locale).getString(nameKey);
        }
    }

    public OperationResponse() {
    }

    public OperationResponse(Operation operation, String messageKey, long objectId, Locale locale) {
        this.responseLocale = locale;
        this.operation = operation.getLocalizedOperationName(locale);
        this.objectId = objectId;
        this.message = ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                locale).getString(messageKey) + StringUtils.SPACE + objectId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getObjectId() {
        return objectId;
    }

    public Locale getResponseLocale() {
        return responseLocale;
    }
}
