package com.mycompany.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@Service
public class TokenAuthenticationService {

    @Value("${jwt.header}")
    private String jwtHeader;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    public ResponseEntity addAuthentication(String userId) {
        return ResponseEntity.ok().header(jwtHeader, jwtTokenHelper.generateToken(userId)).build();
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(jwtHeader);
        if (token != null) {
            String userId = jwtTokenHelper.parseUserFromToken(token);
            if (userId != null) {
                return new AuthUser(userId);
            }
        }
        return null;
    }
}
