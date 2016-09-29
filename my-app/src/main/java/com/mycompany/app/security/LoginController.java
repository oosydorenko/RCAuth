package com.mycompany.app.security;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


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

    @Value("${jwt.userId}")
    private String userIdField;

    @RequestMapping("/auth")
    public ResponseEntity home(HttpServletRequest rq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity responseEntity;
        if (!(auth.getDetails() instanceof String)) {
            SecurityContextHolder.getContext().setAuthentication(null);
            JsonNode node = restTemplate.getForObject(resourceURI, JsonNode.class);
            String userId = node.get(userIdField).asText();
            responseEntity = tokenAuthenticationService.addAuthentication(userId);
            SecurityContextHolder.getContext().setAuthentication(new AuthUser(userId));
        } else {
            responseEntity = ResponseEntity.ok().header(jwtHeader, rq.getHeader(jwtHeader)).build();
        }
        return responseEntity;

    }

    @RequestMapping("/profile")
    @ResponseBody
    public JsonNode userDetails() {
        return restTemplate.getForObject(resourceURI, JsonNode.class);
    }
}
