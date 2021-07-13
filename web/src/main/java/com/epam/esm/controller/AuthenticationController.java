package com.epam.esm.controller;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserCredential;
import com.epam.esm.exception.InvalidUserCredentialException;
import com.epam.esm.response.OperationResponse;
import com.epam.esm.security.JwtTokenProvider;
import com.epam.esm.service.UserService;
import com.epam.esm.util.MessageLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService<User> service;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserService<User> service, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public OperationResponse login(HttpServletRequest request, @RequestBody UserCredential credential) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
            User user = service.findByEmail(credential.getEmail());
            String token = jwtTokenProvider.createToken(credential.getEmail(), user.getRole().name());
            return new OperationResponse(OperationResponse.Operation.AUTHORIZATION,
                    ResponseMessageName.USER_LOGIN_OPERATION, user.getId(),
                    MessageLocale.defineLocale(request.getHeader(HeaderName.LOCALE)), token);
        } catch (AuthenticationException e) {
            throw new InvalidUserCredentialException(ErrorCode.AUTHENTICATION, ErrorName.INVALID_USER_CREDENTIAL,
                    credential.getEmail());
        }
    }
}
