package com.epam.esm.controller;

import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.dto.Tag;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.response.OperationResponse;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagService<Tag> tagService;
    private final GiftCertificateService<GiftCertificate> certificateService;
    private final Hateoas<Tag> tagHateoas;
    private final Hateoas<OperationResponse> responseHateoas;

    @Autowired
    public TagController(TagService<Tag> tagService, Hateoas<Tag> tagHateoas, GiftCertificateService<GiftCertificate>
            certificateService, @Qualifier("tagOperationResponseHateoas") Hateoas<OperationResponse> responseHateoas) {
        this.tagService = tagService;
        this.certificateService = certificateService;
        this.tagHateoas = tagHateoas;
        this.responseHateoas = responseHateoas;
    }

    @GetMapping
    public List<Tag> findAllTags(@RequestParam int page, @RequestParam int elements) {
        List<Tag> tags = tagService.findAll(page, elements);
        tags.forEach(tagHateoas::createHateoas);
        return tags;
    }

    @GetMapping("/{id}")
    public Tag findTagById(@PathVariable String id) {
        Tag tag = tagService.findById(id);
        tagHateoas.createHateoas(tag);
        return tag;
    }

    @DeleteMapping("/{id}")
    public OperationResponse deleteTag(@PathVariable String id) {
        certificateService.disconnectTagById(id);
        tagService.delete(id);
        OperationResponse response = new OperationResponse(OperationResponse.Operation.DELETION,
                ResponseMessageName.TAG_DELETE_OPERATION, Long.parseLong(id));
        responseHateoas.createHateoas(response);
        return response;
    }

    @PostMapping("/new")
    public OperationResponse createTag(@RequestBody Tag tag) {
        OperationResponse response = new OperationResponse(OperationResponse.Operation.CREATION,
                ResponseMessageName.TAG_CREATE_OPERATION, tagService.insert(tag));
        responseHateoas.createHateoas(response);
        return response;
    }
}
