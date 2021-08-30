package com.epam.esm.controller;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dto.User;
import com.epam.esm.dto.UserCredential;
import com.epam.esm.exception.InvalidUserCredentialException;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.response.AuthenticationOperationResponse;
import com.epam.esm.response.EntityOperationResponse;
import com.epam.esm.jwt.JwtProvider;
import com.epam.esm.service.UserService;
import com.epam.esm.util.MessageLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final JwtProvider jwtProvider;
    private final Hateoas<EntityOperationResponse> hateoas;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserService<User> service,
                                    JwtProvider jwtProvider, @Qualifier("userOperationResponseHateoas")
                                            Hateoas<EntityOperationResponse> hateoas) {
        this.authenticationManager = authenticationManager;
        this.service = service;
        this.jwtProvider = jwtProvider;
        this.hateoas = hateoas;
    }

    @PostMapping("/login")
    public AuthenticationOperationResponse login(HttpServletRequest request, @RequestBody UserCredential credential) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
            User user = service.findByEmail(credential.getEmail());
            String token = jwtProvider.createToken(credential.getEmail(), user.getRole().name());
            return new AuthenticationOperationResponse(EntityOperationResponse.Operation.AUTHORIZATION,
                    ResponseMessageName.USER_LOGIN_OPERATION, user.getId(),
                    MessageLocale.defineLocale(request.getHeader(HeaderName.LOCALE)), token);
        } catch (AuthenticationException e) {
            throw new InvalidUserCredentialException(ErrorCode.AUTHENTICATION, ErrorName.INVALID_USER_CREDENTIAL,
                    credential.getEmail());
        }
    }

    @PostMapping("/logout")
    public EntityOperationResponse logout(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        jwtProvider.removeToken(token);
        return (new EntityOperationResponse(EntityOperationResponse.Operation.LOGOUT,
                ResponseMessageName.USER_LOGOUT_OPERATION, service.findByEmail(jwtProvider.getUserName(token)).getId(),
                MessageLocale.defineLocale(request.getHeader(HeaderName.LOCALE))));
    }

    @PostMapping("/register")
    public EntityOperationResponse register(HttpServletRequest request, @RequestBody User user) {
        EntityOperationResponse response = new EntityOperationResponse(EntityOperationResponse.Operation.CREATION,
                ResponseMessageName.USER_REGISTER_OPERATION, service.insert(user), MessageLocale.defineLocale(
                request.getHeader(HeaderName.LOCALE)));
        hateoas.createHateoas(response);
        return response;
    }
}
