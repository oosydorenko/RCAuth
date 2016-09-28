package com.mycompany.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig {

    @Autowired
    private Environment env;

    @Bean
    public OAuth2ProtectedResourceDetails amznResource() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setId("amazon-oauth-client");
        details.setClientId(env.getProperty("amzn.client.id"));
        details.setClientSecret(env.getProperty("amzn.client.secret"));
        details.setAccessTokenUri(env.getProperty("amzn.accessTokenUri"));
        details.setUserAuthorizationUri(env.getProperty("amzn.userAuthorizationUri"));
        details.setTokenName(env.getProperty("amzn.authorization.code"));
        details.setScope(parseScopes(env.getProperty("amzn.auth.scope")));
        //details.setPreEstablishedRedirectUri(env.getProperty("amzn.preestablished.redirect.url"));
        return details;
    }

    private List<String> parseScopes(String commaSeparatedScopes) {
        List<String> scopes = new ArrayList<>();
        Collections.addAll(scopes, commaSeparatedScopes.split(","));
        return scopes;
    }

    @Bean
    public OAuth2RestTemplate restTemplate(OAuth2ClientContext oauth2ClientContext) {
        return new OAuth2RestTemplate(amznResource(), oauth2ClientContext);
    }

}
