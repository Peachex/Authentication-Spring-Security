package com.epam.esm.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.response.EntityOperationResponse;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderOperationResponseHateoas implements Hateoas<EntityOperationResponse> {
    @Override
    public void createHateoas(EntityOperationResponse response) {
        if (response.getLinks().isEmpty()) {
            response.add(linkTo(methodOn(GiftCertificateController.class).findCertificateById(String.valueOf(response
                    .getObjectId()))).withSelfRel());
        }
    }
}
