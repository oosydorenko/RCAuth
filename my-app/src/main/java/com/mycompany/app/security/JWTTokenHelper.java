package com.mycompany.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@Component
public class JWTTokenHelper {

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.userId}")
    private String userIdField;

    @Value("${jwt.refreshCode}")
    private String refreshTokenField;


    public String parseUserFromToken(String token) {
        String userId;
        try {
            final Claims claims = getClaimsFromToken(token);
            userId = (String) claims.get(userIdField);
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    public String getRefreshCodeFromToken(String token) {
        String refresh_code;
        try {
            final Claims claims = getClaimsFromToken(token);
            refresh_code = (String) claims.get(refreshTokenField);
        } catch (Exception e) {
            refresh_code = null;
        }
        return refresh_code;
    }

    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(userIdField, userId);
        claims.put(refreshTokenField, restTemplate.getAccessToken().getRefreshToken());
        return generateToken(claims);
    }

    String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


}
