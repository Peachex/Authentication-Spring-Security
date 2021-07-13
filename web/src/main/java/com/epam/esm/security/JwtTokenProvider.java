package com.epam.esm.security;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.constant.entity.UserFieldName;
import com.epam.esm.constant.error.ErrorCode;
import com.epam.esm.constant.error.ErrorName;
import com.epam.esm.dto.User;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.Date;

@Component
@PropertySource("classpath:security/jwt.properties")
public class JwtTokenProvider {
    private final SecretKey secretKey = MacProvider.generateKey();
    private final UserService<User> service;
    @Value("${jwt.expirationInHours}")
    private int expirationInHours;

    @Autowired
    public JwtTokenProvider(UserService<User> service) {
        this.service = service;
    }

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put(UserFieldName.ROLE, role);
        Date now = new Date();
        Date validity = Date.from(now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .plusHours(expirationInHours).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(ErrorCode.USER, ErrorName.INVALID_AUTH_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = service.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(HeaderName.AUTHORIZATION);
    }
}
