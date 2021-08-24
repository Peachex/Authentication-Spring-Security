package com.epam.esm.response;

import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.constant.PropertyFileName;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public class EntityOperationResponse extends Response {
    private final String operation;
    private final long objectId;
    private final Locale responseLocale;

    public enum Operation {
        CREATION(ResponseMessageName.CREATION_OPERATION),
        DELETION(ResponseMessageName.DELETION_OPERATION),
        UPDATE(ResponseMessageName.UPDATE_OPERATION),
        AUTHORIZATION(ResponseMessageName.AUTHORIZATION_OPERATION);

        private final String nameKey;

        Operation(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getLocalizedOperationName(Locale locale) {
            return ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES, locale).getString(nameKey);
        }
    }

    public EntityOperationResponse(Operation operation, String messageKey, long objectId, Locale locale) {
        super(ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                locale).getString(messageKey) + StringUtils.SPACE + objectId);
        this.responseLocale = locale;
        this.operation = operation.getLocalizedOperationName(locale);
        this.objectId = objectId;
    }

    public String getOperation() {
        return operation;
    }

    public long getObjectId() {
        return objectId;
    }

    public Locale getResponseLocale() {
        return responseLocale;
    }
}
