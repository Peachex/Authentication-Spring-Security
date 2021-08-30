package com.epam.esm.response;

import org.springframework.hateoas.RepresentationModel;

public abstract class Response extends RepresentationModel<EntityOperationResponse> {
    private String message;

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
