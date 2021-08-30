package com.epam.esm.handler;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.response.ExceptionResponse;
import com.epam.esm.util.MessageLocale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class JwtAuthenticationExceptionHandler {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    @ExceptionHandler(JwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleRuntimeExceptions(JwtAuthenticationException e,
                                                                           HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getLocalizedMessage(
                MessageLocale.defineLocale(request.getHeader(HeaderName.LOCALE))), e.getErrorCode());
        exceptionResponse.setErrorCode(status.value() + e.getErrorCode());
        return new ResponseEntity<>(exceptionResponse, status);
    }
}
