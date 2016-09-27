package com.mycompany.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@Service
public class TokenAuthenticationService {

    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    @Autowired
    private JWTTokenHelper jwtTokenHelper;


    public ResponseEntity addAuthentication(String userId) {
        return ResponseEntity.ok().header("AUTH_HEADER_NAME",jwtTokenHelper.generateToken(userId)).build();

    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null) {
            String userId = jwtTokenHelper.parseUserFromToken(token);
            if (userId != null) {
                return new AuthUser(userId);
            }
        }
        return null;
    }
}
