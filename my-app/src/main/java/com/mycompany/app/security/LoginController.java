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

    @RequestMapping("/auth")
    public ResponseEntity home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JsonNode node = null;
        if (auth == null) {
            node = restTemplate.getForObject(resourceURI, JsonNode.class);
            SecurityContextHolder.getContext().setAuthentication(new AuthUser(node.get("user_id").asText()));
            return tokenAuthenticationService.addAuthentication(node.get("user_id").asText());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/auth/login")
    public void login(UsernamePasswordAuthenticationToken principal, OAuth2Authentication auth2Authentication) {
        principal.getCredentials();

    }


}
