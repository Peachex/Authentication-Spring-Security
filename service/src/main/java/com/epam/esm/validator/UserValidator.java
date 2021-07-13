package com.epam.esm.validator;

import com.epam.esm.dto.User;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Zа-яА-Я]{1,256}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("((\\w)([-.](\\w))?){1,64}@((\\w)([-.](\\w))?){1,251}.[a-zA-Z]{2,4}");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("[а-яА-Я\\w\\s\\p{Punct}]{6,256}");

    private UserValidator() {
    }

    public boolean isUserValid(User user) {
        return (isNameValid(user.getFirstName()) && isNameValid(user.getLastName()) && isEmailValid(user.getEmail()) &&
                isPasswordValid(user.getPassword()) && user.getRole() == null && !user.isActive() && user.getId() == 0); //todo maybe bug with isActive
    }

    public boolean isNameValid(String name) {
        return (name != null && NAME_PATTERN.matcher(name).matches());
    }

    public boolean isEmailValid(String email) {
        return (email != null && EMAIL_PATTERN.matcher(email).matches());
    }

    public boolean isPasswordValid(String password) {
        return (password != null && PASSWORD_PATTERN.matcher(password).matches());
    }
}
