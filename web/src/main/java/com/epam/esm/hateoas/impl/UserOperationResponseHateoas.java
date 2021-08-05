package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.response.OperationResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserOperationResponseHateoas implements Hateoas<OperationResponse> {
    private static final List<OperationResponse.Operation> operations = new ArrayList<>();

    static {
        operations.add(OperationResponse.Operation.CREATION);
    }

    @Override
    public void createHateoas(OperationResponse response) {
        if (operations.stream().anyMatch(o -> o.getLocalizedOperationName(response.getResponseLocale())
                .equalsIgnoreCase(response.getOperation()))) {
            response.add(linkTo(methodOn(UserController.class).findUserById(String.valueOf(response.getObjectId())))
                    .withSelfRel());
        }
    }
}
