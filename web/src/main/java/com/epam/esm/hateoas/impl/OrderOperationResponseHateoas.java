package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.response.OperationResponse;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderOperationResponseHateoas implements Hateoas<OperationResponse> {
    @Override
    public void createHateoas(OperationResponse response) {
        if (response.getLinks().isEmpty()) {
            response.add(linkTo(methodOn(GiftCertificateController.class).findCertificateById(String.valueOf(response
                    .getObjectId()))).withSelfRel());
        }
    }
}
