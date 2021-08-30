package com.epam.esm.jwt;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.response.ExceptionResponse;
import com.epam.esm.util.JsonConverter;
import com.epam.esm.util.MessageLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends GenericFilterBean {
    private final JwtProvider provider;
    private final JsonConverter jsonConverter;

    @Autowired
    public JwtFilter(JwtProvider provider, JsonConverter jsonConverter) {
        this.provider = provider;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = provider.resolveToken((HttpServletRequest) request);
        try {
            if (token != null && provider.validateToken(token)) {
                Authentication authentication = provider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            chain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            provider.removeToken(token);
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            ExceptionResponse exceptionResponse = new ExceptionResponse(e.getLocalizedMessage(MessageLocale.defineLocale(
                    httpRequest.getHeader(HeaderName.LOCALE))), e.getErrorCode());
            exceptionResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value() + e.getErrorCode());

            ResponseEntity<ExceptionResponse> responseEntity = new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
            httpResponse.getOutputStream().println(jsonConverter.convert(responseEntity));
        }
    }
}
