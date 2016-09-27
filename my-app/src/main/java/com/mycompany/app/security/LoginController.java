package com.mycompany.app.security;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@RestController
class LoginController {

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Value("${config.oauth2.resourceURI}")
    private String resourceURI;

    @Value("${jwt.header}")
    private String jwtHeader;

    @RequestMapping("/auth")
    public ResponseEntity home(HttpServletRequest rq, HttpServletResponse rsp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity responseEntity;
        if (auth == null) {
            JsonNode node = restTemplate.getForObject(resourceURI, JsonNode.class);
            responseEntity = tokenAuthenticationService.addAuthentication(node.get("user_id").asText());
        } else {
            responseEntity = ResponseEntity.ok().header(jwtHeader, rq.getHeader(jwtHeader)).build();
        }
        return responseEntity;

    }
}
