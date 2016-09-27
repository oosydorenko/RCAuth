package com.mycompany.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientContextFilter oAuth2ClientContextFilter;

    @Autowired
    private OAuth2RestOperations restTemplate;

    private final TokenAuthenticationService tokenAuthenticationService;

    public SecurityConfig() {
        super(true);
        tokenAuthenticationService = new TokenAuthenticationService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Custom Token based authentication based on the header previously given to the client
                .addFilterBefore(new AuthenticationTokenFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(oAuth2ClientContextFilter, SecurityContextPersistenceFilter.class);
                //.addFilterBefore(oAuth2ClientAuthenticationProcessingFilter(), AuthenticationTokenFilter.class);
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return tokenAuthenticationService;
    }

    @Bean
    public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter() {
        OAuth2ClientAuthenticationProcessingFilter oauth = new OAuth2ClientAuthenticationProcessingFilter("/auth");
        oauth.setRestTemplate(restTemplate);
        return oauth;
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint(){
        return new LoginUrlAuthenticationEntryPoint("/auth");
    }


}
