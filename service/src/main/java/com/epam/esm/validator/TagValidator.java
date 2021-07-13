package com.epam.esm.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TagValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("[А-Яа-я\\w\\s\\p{Punct}]{1,256}");

    private TagValidator() {
    }

    public boolean isNameValid(String name) {
        return (name != null && NAME_PATTERN.matcher(name).matches());
    }
}
