package com.epam.esm.response;

import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.constant.PropertyFileName;
import com.epam.esm.util.MessageLocale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.RepresentationModel;

import java.util.ResourceBundle;

public class OperationResponse extends RepresentationModel<OperationResponse> {
    private String operation;
    private String message;
    private long objectId;

    public enum Operation {
        CREATION(ResponseMessageName.CREATION_OPERATION),
        DELETION(ResponseMessageName.DELETION_OPERATION),
        UPDATE(ResponseMessageName.UPDATE_OPERATION),
        OTHER(ResponseMessageName.OTHER_OPERATION);

        private final String nameKey;

        Operation(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getLocalizedOperationName() {
            return ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                    MessageLocale.getCurrent()).getString(nameKey);
        }
    }

    public OperationResponse() {
    }

    public OperationResponse(Operation operation, String messageKey) {
        this.operation = operation.getLocalizedOperationName();
        this.message = ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                MessageLocale.getCurrent()).getString(messageKey);
    }

    public OperationResponse(Operation operation, String messageKey, long objectId) {
        this.operation = operation.getLocalizedOperationName();
        this.objectId = objectId;
        this.message = ResourceBundle.getBundle(PropertyFileName.OPERATION_RESPONSE_MESSAGES,
                MessageLocale.getCurrent()).getString(messageKey) + StringUtils.SPACE + objectId;
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
}
